package uz.yalla.client.feature.order.presentation.search.view

import androidx.compose.ui.unit.Dp

sealed interface SearchCarSheetIntent {
    data class OnCancelled(val orderId: Int?) : SearchCarSheetIntent
    data object AddNewOrder : SearchCarSheetIntent
    data object ZoomOut : SearchCarSheetIntent
    data class SetHeaderHeight(val height: Dp) : SearchCarSheetIntent
    data class SetFooterHeight(val height: Dp) : SearchCarSheetIntent
}