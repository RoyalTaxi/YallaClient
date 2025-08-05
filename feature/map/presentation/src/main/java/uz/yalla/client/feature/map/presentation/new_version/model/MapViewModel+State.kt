package uz.yalla.client.feature.map.presentation.new_version.model

import uz.yalla.client.core.common.marker.YallaMarkerState

fun MViewModel.clearState() {
    staticPrefs.processingOrderId = null
    updateState { state ->
        state.copy(
            order = null,
            orderId = null,
            location = null,
            destinations = emptyList(),
            route = emptyList(),
            markerState = YallaMarkerState.LOADING
        )
    }
}


fun MViewModel.removeLastDestination() {
    if (stateFlow.value.destinations.isNotEmpty()) {
        updateState {
            it.copy(
                destinations = it.destinations.dropLast(1)
            )
        }
    }
}

fun MViewModel.setPermissionDialog(visible: Boolean) {
    updateState { state ->
        state.copy(permissionDialogVisible = visible)
    }
}

fun MViewModel.setLocationEnabled(enabled: Boolean) {
    updateState { state ->
        state.copy(locationEnabled = enabled)
    }
}

fun MViewModel.setLocationGranted(granted: Boolean) {
    updateState { state ->
        state.copy(locationGranted = granted)
    }
}
