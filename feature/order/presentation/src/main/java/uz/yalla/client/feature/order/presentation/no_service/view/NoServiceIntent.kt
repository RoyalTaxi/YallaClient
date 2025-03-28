package uz.yalla.client.feature.order.presentation.no_service.view

import androidx.compose.ui.unit.Dp
import uz.yalla.client.core.domain.model.SelectedLocation

sealed interface NoServiceIntent {
    data class SetSelectedLocation(val location: SelectedLocation) : NoServiceIntent
    data class SetSheetHeight(val height: Dp) : NoServiceIntent
}