package uz.yalla.client.feature.order.presentation.client_waiting.view

import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingIntent

sealed interface ClientWaitingIntent {
    data class OnCancelled(val orderId: Int?) : ClientWaitingIntent
    data object AddNewOrder : ClientWaitingIntent
}