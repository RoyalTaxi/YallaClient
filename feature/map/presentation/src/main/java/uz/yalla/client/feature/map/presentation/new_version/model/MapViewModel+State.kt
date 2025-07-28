package uz.yalla.client.feature.map.presentation.new_version.model


fun MViewModel.clearState() = intent {
    reduce {
        state.copy(
            order = null,
            orderId = null
        )
    }
}

fun MViewModel.removeLastDestination() = intent {
    if (state.destinations.isNotEmpty())
        reduce { state.copy(destinations = state.destinations.dropLast(1)) }
}

fun MViewModel.setPermissionDialog(visible: Boolean) = intent {
    reduce { state.copy(permissionDialogVisible = visible) }
}

fun MViewModel.setLocationEnabled(enabled: Boolean) = intent {
    reduce { state.copy(locationEnabled = enabled) }
}

fun MViewModel.setLocationGranted(granted: Boolean) = intent {
    reduce { state.copy(locationGranted = granted) }
}
