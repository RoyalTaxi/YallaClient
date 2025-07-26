package uz.yalla.client.feature.map.presentation.new_version.model

import androidx.lifecycle.viewModelScope
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
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import kotlin.time.Duration.Companion.seconds

fun MViewModel.startObserve() {
    observerScope.launch { observeActiveOrders() }
    observerScope.launch { observeCameraState() }
    observerScope.launch { observeLocation() }
    observerScope.launch { observeDestination() }
    observerScope.launch { observeLocations() }
    observerScope.launch { observeActiveOrder() }
    observerScope.launch { observeSheetCoordinator() }
    observerScope.launch { observeRoute() }
    observerScope.launch { observeInfoMarkers() }
}

fun MViewModel.observeInfoMarkers() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { Pair(it.carArrivalInMinutes, it.orderEndsInMinutes) }
        .collectLatest { state ->
            mapsViewModel.onIntent(MapsIntent.UpdateCarArrivesInMinutes(state.carArrivalInMinutes))
            mapsViewModel.onIntent(MapsIntent.UpdateOrderEndsInMinutes(state.orderEndsInMinutes))
        }
}

fun MViewModel.observeActiveOrder() = viewModelScope.launch {
    while (isActive) {
        getActiveOrder()
        delay(10.seconds)
    }
}

fun MViewModel.observeActiveOrders() = viewModelScope.launch {
    while (isActive) {
        getActiveOrders()
        delay(10.seconds)
    }
}

fun MViewModel.observeCameraState() = viewModelScope.launch {
    mapsViewModel.cameraState.collectLatest { markerState ->
        intent {
            if (markerState.isMoving) reduce {
                state.copy(
                    markerLocation = null,
                    markerState = YallaMarkerState.LOADING
                )
            } else {
                reduce {
                    state.copy(
                        markerLocation = state.markerLocation?.copy(point = markerState.position)
                    )
                }

                getAddress(markerState.position)
            }

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
        }
    }
}

fun MViewModel.observeLocation() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { it.location }
        .collectLatest { state ->
            state.location?.let { MainSheetChannel.setLocation(it) }
        }
}

fun MViewModel.observeDestination() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { it.destinations }
        .collectLatest { state ->
            MainSheetChannel.setDestination(state.destinations)
        }
}

fun MViewModel.observeLocations() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { it.location to it.destinations }
        .collectLatest { state ->
            getRouting()
            mapsViewModel.onIntent(
                MapsIntent.UpdateLocations(
                    listOfNotNull(state.location?.point) + state.destinations.mapNotNull { it.point }
                )
            )
        }
}

fun MViewModel.observeRoute() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { it.route }
        .collectLatest { state ->
            mapsViewModel.onIntent(MapsIntent.UpdateRoute(state.route))

            intent {
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

fun MViewModel.observeSheetCoordinator() = viewModelScope.launch {
    SheetCoordinator.currentSheetState.collectLatest { sheetState ->
        sheetState?.height?.let { height ->
            intent { reduce { state.copy(overlayPadding = height, sheetHeight = height) } }
            mapsViewModel.onIntent(MapsIntent.SetBottomPadding(height))
        }
    }
}

fun MViewModel.stopObserve() = viewModelScope.launch {
    cancelable.forEach { it.cancel() }
}