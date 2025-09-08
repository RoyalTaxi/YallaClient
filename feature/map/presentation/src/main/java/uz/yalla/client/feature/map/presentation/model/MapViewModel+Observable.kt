package uz.yalla.client.feature.map.presentation.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.isActive
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.new_map.core.MyMapIntent
import uz.yalla.client.core.common.state.CameraButtonState
import uz.yalla.client.core.common.state.CameraButtonState.MyLocationView
import uz.yalla.client.core.common.state.CameraButtonState.MyRouteView
import uz.yalla.client.core.common.state.NavigationButtonState
import uz.yalla.client.feature.order.domain.model.response.order.toCommonExecutor
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import uz.yalla.client.feature.order.presentation.no_service.NO_SERVICE_ROUTE
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds


suspend fun MViewModel.pollActiveOrder() {
    stateFlow
        .distinctUntilChangedBy { it.orderId }
        .collectLatest {
            while (coroutineContext.isActive) {
                getActiveOrder()
                delay(10.seconds)
            }
        }
}

suspend fun MViewModel.pollActiveOrders() {
    stateFlow
        .distinctUntilChangedBy { it.orderId }
        .collectLatest {
            while (coroutineContext.isActive) {
                getActiveOrders()
                delay(10.seconds)
            }
        }
}

suspend fun MViewModel.observeMarkerState() {
    mapsViewModel.markerState.collectLatest { markerState ->
        stateFlow.value.let { state ->
            if (state.order == null && state.destinations.isEmpty()) {
                if (markerState.isMoving) {
                    updateState { it.copy(markerState = YallaMarkerState.LOADING) }
                } else {
                    getAddress(markerState.point)
                }
            }

            if (markerState.isMoving.not()) when {
                state.cameraButtonState == MyRouteView && markerState.isByUser -> {
                    updateState { it.copy(cameraButtonState = MyRouteView) }
                }

                state.cameraButtonState == MyRouteView && !markerState.isByUser -> {
                    updateState { it.copy(cameraButtonState = CameraButtonState.FirstLocation) }
                }

                state.cameraButtonState == CameraButtonState.FirstLocation -> {
                    updateState { it.copy(cameraButtonState = MyRouteView) }
                }
            }
        }
    }
}

suspend fun MViewModel.observeSheetCoordinator() {
    SheetCoordinator.currentSheetState.collectLatest { sheetState ->
        if (sheetState?.route == NO_SERVICE_ROUTE) sheetState.height.let { height ->
            updateState { it.copy(overlayPadding = height) }
        } else sheetState?.height?.let { height ->
            updateState { it.copy(overlayPadding = height, sheetHeight = height) }
        }
    }
}

suspend fun MViewModel.observeNavigationButton() {
    stateFlow
        .distinctUntilChangedBy {
            it.route to it.order
        }
        .collect { state ->
            updateState {
                it.copy(
                    navigationButtonState = when {
                        state.route.isNotEmpty() -> NavigationButtonState.NavigateBack
                        state.order != null -> NavigationButtonState.NavigateBack
                        else -> NavigationButtonState.OpenDrawer
                    }
                )
            }
        }
}


suspend fun MViewModel.observeInfoMarkers() {
    stateFlow
        .distinctUntilChangedBy { Pair(it.carArrivalInMinutes, it.orderEndsInMinutes) }
        .collectLatest { state ->
            mapsViewModel.onIntent(MyMapIntent.SetCarArrivesInMinutes(state.carArrivalInMinutes))
            mapsViewModel.onIntent(MyMapIntent.SetOrderEndsInMinutes(state.orderEndsInMinutes))
        }
}

suspend fun MViewModel.observeLocations() {
    stateFlow
        .distinctUntilChangedBy { it.location to it.destinations }
        .collectLatest { state ->
            val points = listOfNotNull(
                state.location?.point,
                *state.destinations.mapNotNull { it.point }.toTypedArray()
            )

            mapsViewModel.onIntent(MyMapIntent.SetLocations(points))

            state.location?.let { location -> MainSheetChannel.setLocation(location) }
            MainSheetChannel.setDestination(state.destinations)

            getRouting()
        }
}

suspend fun MViewModel.observeRoute() {
    stateFlow
        .distinctUntilChangedBy { Pair(it.route, it.order?.status) }
        .collectLatest { state ->
            mapsViewModel.onIntent(MyMapIntent.SetRoute(state.route))

            refocus()

            updateState {
                it.copy(
                    cameraButtonState = when {
                        state.route.isEmpty() -> MyLocationView
                        else -> MyRouteView
                    }
                )
            }
        }
}

suspend fun MViewModel.observeDrivers() {
    stateFlow
        .distinctUntilChangedBy { it.drivers }
        .collectLatest { state ->
            mapsViewModel.onIntent(MyMapIntent.SetDrivers(state.drivers))
        }
}

suspend fun MViewModel.observeOrder() {
    stateFlow
        .distinctUntilChangedBy { it.order }
        .collectLatest { state ->
            mapsViewModel.onIntent(MyMapIntent.SetDriver(state.order?.executor?.toCommonExecutor()))
            mapsViewModel.onIntent(MyMapIntent.SetOrderStatus(state.order?.status))

            refocus()
        }
}

suspend fun MViewModel.observeMapPadding() {
    stateFlow
        .distinctUntilChangedBy { it.sheetHeight to it.topPadding }
        .collectLatest { state ->
            mapsViewModel.onIntent(
                MyMapIntent.SetViewPadding(
                    PaddingValues(
                        top = stateFlow.value.topPadding.takeIf { it.isSpecified } ?: 0.dp,
                        bottom = stateFlow.value.sheetHeight.takeIf { it.isSpecified } ?: 0.dp
                    )
                )
            )

            refocus()
        }
}
