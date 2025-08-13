package uz.yalla.client.feature.map.presentation.new_version.model

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.maps.MapsIntent
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.CameraButtonState.FirstLocation
import uz.yalla.client.core.common.state.CameraButtonState.MyLocationView
import uz.yalla.client.core.common.state.CameraButtonState.MyRouteView
import uz.yalla.client.core.common.state.NavigationButtonState
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.order.domain.model.response.order.toCommonExecutor
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import uz.yalla.client.feature.order.presentation.no_service.NO_SERVICE_ROUTE
import kotlin.time.Duration.Companion.seconds

fun MViewModel.startObserve() {
    scope.launch { observeActiveOrders() }
    scope.launch { observeMarkerState() }
    scope.launch { observeLocations() }
    scope.launch { observeActiveOrder() }
    scope.launch { observeSheetCoordinator() }
    scope.launch { observeRoute() }
    scope.launch { observeInfoMarkers() }
    scope.launch { observeNavigationButton() }
    scope.launch { observeDriver() }
    scope.launch { observeDrivers() }
}

fun MViewModel.observeInfoMarkers() = viewModelScope.launch {
    stateFlow
        .distinctUntilChangedBy { Pair(it.carArrivalInMinutes, it.orderEndsInMinutes) }
        .collectLatest { state ->
            mapsViewModel.onIntent(MapsIntent.UpdateCarArrivesInMinutes(state.carArrivalInMinutes))
            mapsViewModel.onIntent(MapsIntent.UpdateOrderEndsInMinutes(state.orderEndsInMinutes))
        }
}

fun MViewModel.observeActiveOrder() = viewModelScope.launch {
    stateFlow
        .distinctUntilChangedBy { it.orderId }
        .collectLatest {
            while (isActive) {
                getActiveOrder()
                delay(10.seconds)
            }
        }
}

fun MViewModel.observeActiveOrders() = viewModelScope.launch {
    stateFlow
        .distinctUntilChangedBy { it.orderId }
        .collectLatest {
            while (isActive) {
                getActiveOrders()
                delay(10.seconds)
            }
        }
}

fun MViewModel.observeMarkerState() = viewModelScope.launch {
    mapsViewModel.cameraState.collectLatest { markerState ->
        stateFlow.value.let { state ->
            if (state.order == null && state.destinations.isEmpty()) {
                if (markerState.isMoving) {
                    updateState { it.copy(markerState = YallaMarkerState.LOADING) }
                } else {
                    getAddress(markerState.position)
                }
            }

            if (markerState.isMoving.not()) when {
                state.cameraButtonState == MyRouteView && markerState.isByUser -> {
                    updateState { it.copy(cameraButtonState = MyRouteView) }
                }

                state.cameraButtonState == MyRouteView && !markerState.isByUser -> {
                    updateState { it.copy(cameraButtonState = FirstLocation) }
                }

                state.cameraButtonState == FirstLocation -> {
                    updateState { it.copy(cameraButtonState = MyRouteView) }
                }
            }
        }
    }
}

fun MViewModel.observeLocations() = viewModelScope.launch {
    stateFlow
        .distinctUntilChangedBy { it.location to it.destinations }
        .collectLatest { state ->
            getRouting()
            state.location?.let { location -> MainSheetChannel.setLocation(location) }
            MainSheetChannel.setDestination(state.destinations)
        }
}

fun MViewModel.observeRoute() = viewModelScope.launch {
    stateFlow
        .distinctUntilChangedBy { Pair(it.route, it.order?.status) }
        .collectLatest { state ->
            mapsViewModel.onIntent(MapsIntent.UpdateRoute(state.route))

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

fun MViewModel.observeSheetCoordinator() = viewModelScope.launch {
    SheetCoordinator.currentSheetState.collectLatest { sheetState ->
        if (sheetState?.route == NO_SERVICE_ROUTE) sheetState.height.let { height ->
            updateState { it.copy(overlayPadding = height) }
        } else sheetState?.height?.let { height ->
            updateState { it.copy(overlayPadding = height, sheetHeight = height) }
            mapsViewModel.onIntent(MapsIntent.SetBottomPadding(height))
        }
    }
}

fun MViewModel.observeNavigationButton() = viewModelScope.launch {
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

fun MViewModel.observeDriver() = viewModelScope.launch {
    stateFlow
        .distinctUntilChangedBy { it.order }
        .collectLatest { state ->
            state.order?.let { order ->
                mapsViewModel.onIntent(MapsIntent.UpdateDriver(order.executor.toCommonExecutor()))
            } ?: run {
                mapsViewModel.onIntent(MapsIntent.UpdateDriver(null))
            }

            if (state.order?.status in OrderStatus.nonInteractive) return@collectLatest

            state.order?.taxi?.routes?.firstOrNull()?.let { point ->
                mapsViewModel.onIntent(
                    MapsIntent.MoveTo(
                        MapPoint(point.coords.lat, point.coords.lng)
                    )
                )
            } ?: run {
                state.location?.point?.let { point ->
                    mapsViewModel.onIntent(MapsIntent.MoveTo(point))
                }
            }
        }
}

fun MViewModel.observeDrivers() = viewModelScope.launch {
    stateFlow
        .distinctUntilChangedBy { it.drivers }
        .collectLatest { state ->
            mapsViewModel.onIntent(MapsIntent.UpdateDrivers(state.drivers))
        }
}

fun MViewModel.stopObserve() = viewModelScope.launch {
    cancelable.forEach { it.cancel() }
    scope.cancel()
}
