package uz.yalla.client.feature.home.presentation.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.map.core.intent.MapIntent.SetCarArrivesInMinutes
import uz.yalla.client.core.common.map.core.intent.MapIntent.SetDriver
import uz.yalla.client.core.common.map.core.intent.MapIntent.SetDrivers
import uz.yalla.client.core.common.map.core.intent.MapIntent.SetLocations
import uz.yalla.client.core.common.map.core.intent.MapIntent.SetOrderEndsInMinutes
import uz.yalla.client.core.common.map.core.intent.MapIntent.SetOrderStatus
import uz.yalla.client.core.common.map.core.intent.MapIntent.SetRoute
import uz.yalla.client.core.common.map.core.intent.MapIntent.SetViewPadding
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.CameraButtonState.FirstLocation
import uz.yalla.client.core.common.state.CameraButtonState.MyLocationView
import uz.yalla.client.core.common.state.CameraButtonState.MyRouteView
import uz.yalla.client.core.common.state.NavigationButtonState
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet
import uz.yalla.client.feature.order.domain.model.response.order.toCommonExecutor
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import uz.yalla.client.feature.order.presentation.no_service.NO_SERVICE_ROUTE
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheetChannel
import kotlin.time.Duration.Companion.seconds


fun HomeViewModel.pollActiveOrder() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { it.orderId }
        .collectLatest {
            while (coroutineContext.isActive) {
                getActiveOrder()
                delay(10.seconds)
            }
        }
}

fun HomeViewModel.pollActiveOrders() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { it.orderId }
        .collectLatest {
            while (coroutineContext.isActive) {
                getActiveOrders()
                delay(10.seconds)
            }
        }
}

@OptIn(FlowPreview::class)
fun HomeViewModel.observeMarkerState() = intent {
    repeatOnSubscription {
        mapsViewModel.markerState.collectLatest { markerState ->
            if (markerState.isMoving.not()) {
                when {
                    state.cameraButtonState == MyRouteView && markerState.isByUser -> {
                        reduce { state.copy(cameraButtonState = MyRouteView) }
                    }

                    state.cameraButtonState == MyRouteView && !markerState.isByUser -> {
                        reduce { state.copy(cameraButtonState = FirstLocation) }
                    }

                    state.cameraButtonState == FirstLocation -> {
                        reduce { state.copy(cameraButtonState = MyRouteView) }
                    }
                }
            }

            if (state.order == null && state.destinations.isEmpty()) {
                if (markerState.isMoving) {
                    reduce { state.copy(markerState = YallaMarkerState.LOADING) }
                } else {
                    getAddress(markerState.point)
                }
            }
        }
    }
}

fun HomeViewModel.observeSheetCoordinator() = viewModelScope.launch {
    SheetCoordinator.currentSheetState.collectLatest { sheetState ->
        if (sheetState?.route == NO_SERVICE_ROUTE) sheetState.height.let { height ->
            intent { reduce { state.copy(overlayPadding = height) } }
        } else sheetState?.height?.let { height ->
            intent { reduce { state.copy(overlayPadding = height, sheetHeight = height) } }
        }
    }
}

fun HomeViewModel.observeNavigationButton() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { it.route to it.order }
        .collectLatest { state ->
            intent {
                reduce {
                    state.copy(
                        navigationButtonState = when {
                            state.route.isNotEmpty() -> NavigationButtonState.NavigateBack
                            state.order != null -> NavigationButtonState.NavigateBack
                            else -> NavigationButtonState.OpenDrawer
                        }
                    )
                }
            }
        }
}


fun HomeViewModel.observeInfoMarkers() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { Pair(it.carArrivalInMinutes, it.orderEndsInMinutes) }
        .collectLatest { state ->
            mapsViewModel.onIntent(SetCarArrivesInMinutes(state.carArrivalInMinutes))
            mapsViewModel.onIntent(SetOrderEndsInMinutes(state.orderEndsInMinutes))
        }
}

fun HomeViewModel.observeLocations() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { it.location to it.destinations }
        .collectLatest { state ->
            val points = listOfNotNull(
                state.location?.point,
                *state.destinations.mapNotNull { it.point }.toTypedArray()
            )

            mapsViewModel.onIntent(SetLocations(points))

            state.location?.let { location ->
                MainSheetChannel.setLocation(location)
                NoServiceSheetChannel.setLocation(location)
            }
            MainSheetChannel.setDestination(state.destinations)

            getRouting()
        }
}

fun HomeViewModel.observeRoute() = intent {
    repeatOnSubscription {
        container.stateFlow
            .distinctUntilChangedBy { Pair(it.route, it.order?.status) }
            .collectLatest {
                mapsViewModel.onIntent(SetRoute(state.route))
                refocus()
                reduce {
                    state.copy(
                        cameraButtonState = when {
                            state.route.isEmpty() -> MyLocationView
                            else -> MyRouteView
                        }
                    )
                }
            }
    }
}

fun HomeViewModel.observeDrivers() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { it.drivers }
        .collectLatest { state ->
            mapsViewModel.onIntent(SetDrivers(state.drivers))
        }
}

fun HomeViewModel.observeOrder() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { it.order }
        .collectLatest { state ->
            mapsViewModel.onIntent(SetDriver(state.order?.executor?.toCommonExecutor()))
            mapsViewModel.onIntent(SetOrderStatus(state.order?.status))
            refocus()
        }
}

fun HomeViewModel.observeViewPadding() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { it.sheetHeight to it.topPadding }
        .collectLatest { state ->
            mapsViewModel.onIntent(
                SetViewPadding(
                    PaddingValues(
                        top = state.topPadding.takeIf { it.isSpecified } ?: 0.dp,
                        bottom = state.sheetHeight.takeIf { it.isSpecified } ?: 0.dp
                    )
                )
            )

            refocus()
        }
}

fun HomeViewModel.observeSheetComputation() = viewModelScope.launch {
    container.stateFlow
        .map(::computeSheet)
        .distinctUntilChanged()
        .collectLatest { computed ->
            val current = sheetFlow.value
            if (current is OrderSheet.CancelReason) return@collectLatest
            setSheet(computed)
        }
}
