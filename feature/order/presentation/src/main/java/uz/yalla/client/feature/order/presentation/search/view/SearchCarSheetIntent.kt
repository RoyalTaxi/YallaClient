package uz.yalla.client.feature.order.presentation.search.view

sealed interface SearchCarSheetIntent {
    data class OnCancelled(val orderId: Int?) : SearchCarSheetIntent
    data object AddNewOrder : SearchCarSheetIntent
    data object ZoomOut : SearchCarSheetIntent
}