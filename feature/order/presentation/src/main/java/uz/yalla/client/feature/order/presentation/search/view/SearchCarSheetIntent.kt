package uz.yalla.client.feature.order.presentation.search.view

import androidx.compose.ui.unit.Dp

sealed interface SearchCarSheetIntent {
    data object ClickCancel : SearchCarSheetIntent
    data object ClickDetails : SearchCarSheetIntent
    data class SetSheetHeight(val height: Dp) : SearchCarSheetIntent
}