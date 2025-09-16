package uz.yalla.client.core.common.sheet.select_from_map.model

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

suspend fun SelectFromMapViewModel.observeMarkerState() {
    liteMapViewModel.markerState.collect { markerState ->
        if (markerState.isMoving) {
            intent { reduce { state.copy(isWorking = false, location = null) } }
        } else {
            viewModelScope.launch { getAddressName(markerState.point) }
            viewModelScope.launch { getIsWorking(markerState.point) }
        }
    }
}