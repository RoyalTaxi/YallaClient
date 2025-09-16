package uz.yalla.client.feature.home.presentation.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import uz.yalla.client.core.common.map.extended.intent.MapIntent
import uz.yalla.client.core.common.map.extended.intent.MapIntent.SetCarArrivesInMinutes
import uz.yalla.client.core.common.map.extended.intent.MapIntent.SetDriver
import uz.yalla.client.core.common.map.extended.intent.MapIntent.SetDrivers
import uz.yalla.client.core.common.map.extended.intent.MapIntent.SetLocations
import uz.yalla.client.core.common.map.extended.intent.MapIntent.SetOrderEndsInMinutes
import uz.yalla.client.core.common.map.extended.intent.MapIntent.SetOrderStatus
import uz.yalla.client.core.common.map.extended.intent.MapIntent.SetRoute
import uz.yalla.client.core.common.map.extended.intent.MapIntent.SetViewPadding
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.CameraButtonState.FirstLocation
import uz.yalla.client.core.common.state.CameraButtonState.MyLocationView
import uz.yalla.client.core.common.state.CameraButtonState.MyRouteView
import uz.yalla.client.core.common.state.NavigationButtonState
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet.CancelReason
import uz.yalla.client.feature.order.domain.model.response.order.toCommonExecutor
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import uz.yalla.client.feature.order.presentation.no_service.NO_SERVICE_ROUTE
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheetChannel
import kotlin.time.Duration.Companion.seconds


suspend fun HomeViewModel.pollActiveOrder() {
    container.stateFlow
        .distinctUntilChangedBy { it.orderId }
        .collectLatest {
            val ctx = currentCoroutineContext()
            while (ctx.isActive) {
                getActiveOrder()
                delay(10.seconds)
            }
        }
}

suspend fun HomeViewModel.pollActiveOrders() {
    container.stateFlow
        .distinctUntilChangedBy { it.orderId }
        .collectLatest {
            val ctx = currentCoroutineContext()
            while (ctx.isActive) {
                getActiveOrders()
                delay(10.seconds)
            }
        }
}

suspend fun HomeViewModel.observeMarkerState() {
    mapViewModel.markerState.collectLatest { markerState ->
        intent {
            if (!markerState.isMoving) {
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

suspend fun HomeViewModel.observeSheetCoordinator() {
    SheetCoordinator.currentSheetState.collectLatest { sheetState ->
        intent {
            if (sheetState?.route == NO_SERVICE_ROUTE) {
                sheetState.height.let { h -> reduce { state.copy(overlayPadding = h) } }
            } else sheetState?.height?.let { h ->
                reduce { state.copy(overlayPadding = h, sheetHeight = h) }
            }
        }
    }
}

suspend fun HomeViewModel.observeNavigationButton() {
    container.stateFlow
        .distinctUntilChangedBy { it.route to it.order }
        .collectLatest { s ->
            intent {
                reduce {
                    state.copy(
                        navigationButtonState =
                            if (s.route.isNotEmpty() || s.order != null)
                                NavigationButtonState.NavigateBack
                            else
                                NavigationButtonState.OpenDrawer
                    )
                }
            }
        }
}

suspend fun HomeViewModel.observeInfoMarkers() {
    container.stateFlow
        .distinctUntilChangedBy { Pair(it.carArrivalInMinutes, it.orderEndsInMinutes) }
        .collectLatest { s ->
            mapViewModel.onIntent(SetCarArrivesInMinutes(s.carArrivalInMinutes))
            mapViewModel.onIntent(SetOrderEndsInMinutes(s.orderEndsInMinutes))
        }
}

suspend fun HomeViewModel.observeLocations() {
    container.stateFlow
        .distinctUntilChangedBy { it.location to it.destinations }
        .collectLatest { s ->
            val points = buildList {
                s.location?.point?.let(::add)
                s.destinations.mapNotNull { it.point }.forEach(::add)
            }

            mapViewModel.onIntent(SetLocations(points))

            s.location?.let { location ->
                MainSheetChannel.setLocation(location)
                if (s.newServiceAvailability == false) NoServiceSheetChannel.setLocation(location)
            }
            MainSheetChannel.setDestination(s.destinations)

            getRouting()
        }
}

suspend fun HomeViewModel.observeRoute() {
    container.stateFlow
        .distinctUntilChangedBy { it.route }
        .collectLatest { state ->
            mapViewModel.onIntent(SetRoute(state.route))
            intent {
                refocus()
                reduce {
                    state.copy(
                        cameraButtonState =
                            if (state.route.isEmpty()) MyLocationView else MyRouteView
                    )
                }
            }
        }
}

suspend fun HomeViewModel.observeDrivers() {
    container.stateFlow
        .distinctUntilChangedBy { it.drivers }
        .collectLatest { s ->
            mapViewModel.onIntent(SetDrivers(s.drivers))
        }
}

suspend fun HomeViewModel.observeOrder() {
    container.stateFlow
        .distinctUntilChangedBy { it.order }
        .collectLatest { state ->
            mapViewModel.onIntent(SetDriver(state.order?.executor?.toCommonExecutor()))
            mapViewModel.onIntent(SetOrderStatus(state.order?.status))
            if (state.order == null) {
                mapViewModel.onIntent(MapIntent.MoveToMyLocation)
            } else if (state.order.status in OrderStatus.nonInteractive) {
                mapViewModel.onIntent(MapIntent.MoveToFirstLocation)
            }
            getRouting()
        }
}

suspend fun HomeViewModel.observeViewPadding() {
    container.stateFlow
        .distinctUntilChangedBy { it.sheetHeight to it.topPadding }
        .collectLatest { s ->
            mapViewModel.onIntent(
                SetViewPadding(
                    PaddingValues(
                        top = s.topPadding.takeIf { it.isSpecified } ?: 0.dp,
                        bottom = s.sheetHeight.takeIf { it.isSpecified } ?: 0.dp
                    )
                )
            )
        }
}

suspend fun HomeViewModel.observeSheetComputation() {
    container.stateFlow
        .map(::computeSheet)
        .distinctUntilChanged()
        .collectLatest { computed ->
            val current = sheetFlow.value
            if (current is CancelReason) return@collectLatest
            setSheet(computed)
        }
}