package uz.yalla.client.feature.map.presentation.view

sealed interface MapOverlayIntent {
    data object MoveToMyLocation : MapOverlayIntent
    data object MoveToFirstLocation : MapOverlayIntent
    data object MoveToMyRoute : MapOverlayIntent
    data object ClickShowOrders : MapOverlayIntent
    data object OnDismissActiveOrders : MapOverlayIntent
    data object OpenDrawer : MapOverlayIntent
    data object NavigateBack : MapOverlayIntent
    data class SetShowingOrder(val orderId: Int) : MapOverlayIntent
}