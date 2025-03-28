package uz.yalla.client.feature.order.presentation.driver_waiting.view

import androidx.compose.ui.unit.Dp
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingIntent
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetIntent

sealed interface DriverWaitingIntent {
    data class OnCancelled(val orderId: Int?) : DriverWaitingIntent
    data class SetSheetHeight(val height: Dp) : DriverWaitingIntent
    data object AddNewOrder : DriverWaitingIntent
}