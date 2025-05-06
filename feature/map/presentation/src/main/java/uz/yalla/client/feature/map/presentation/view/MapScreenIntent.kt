package uz.yalla.client.feature.map.presentation.view

import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

sealed interface MapScreenIntent {
    sealed interface MapOverlayIntent : MapScreenIntent {
        data object AnimateToMyLocation : MapOverlayIntent
        data object OnMapReady : MapOverlayIntent
        data object MoveToFirstLocation : MapOverlayIntent
        data object MoveToMyRoute : MapOverlayIntent
        data object ClickShowOrders : MapOverlayIntent
        data object OpenDrawer : MapOverlayIntent
        data object NavigateBack : MapOverlayIntent
        data object EnableGPS : MapOverlayIntent

    }

    data object OnDismissActiveOrders : MapScreenIntent
    data class SetShowingOrder(val order: ShowOrderModel) : MapScreenIntent
}