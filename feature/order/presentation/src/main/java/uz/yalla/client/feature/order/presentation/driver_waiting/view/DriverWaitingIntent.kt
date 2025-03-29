package uz.yalla.client.feature.order.presentation.driver_waiting.view

sealed interface DriverWaitingIntent {
    data class OnCancelled(val orderId: Int?) : DriverWaitingIntent
    data object AddNewOrder : DriverWaitingIntent
}