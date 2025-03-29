package uz.yalla.client.feature.order.presentation.coordinator

import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SheetCoordinator {
    data class SheetState(val route: String, val height: Dp)

    private val _currentSheetState = MutableStateFlow<SheetState?>(null)
    val currentSheetState = _currentSheetState.asStateFlow()

    fun updateSheetHeight(route: String, height: Dp) {
        _currentSheetState.value = SheetState(route, height)
    }
}