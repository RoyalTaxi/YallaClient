package uz.yalla.client.feature.home.presentation.navigation

import uz.yalla.client.core.domain.model.MapPoint

sealed interface OrderSheet {
    data object Main : OrderSheet
    data object NoService : OrderSheet
    data class Search(val orderId: Int, val point: MapPoint, val tariffId: Int) : OrderSheet
    data class ClientWaiting(val orderId: Int) : OrderSheet
    data class DriverWaiting(val orderId: Int) : OrderSheet
    data class OnTheRide(val orderId: Int) : OrderSheet
    data class Canceled(val orderId: Int) : OrderSheet
    data class Feedback(val orderId: Int) : OrderSheet
    data class CancelReason(val orderId: Int) : OrderSheet
}

