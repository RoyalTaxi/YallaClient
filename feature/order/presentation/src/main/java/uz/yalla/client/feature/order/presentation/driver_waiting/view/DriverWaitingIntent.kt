package uz.yalla.client.feature.order.presentation.driver_waiting.view

import androidx.compose.ui.unit.Dp

sealed interface DriverWaitingIntent {
    data class OnCancelled(val orderId: Int?) : DriverWaitingIntent
    data object AddNewOrder : DriverWaitingIntent
    data class SetHeaderHeight(val height: Dp) : DriverWaitingIntent
    data class SetFooterHeight(val height: Dp) : DriverWaitingIntent
}