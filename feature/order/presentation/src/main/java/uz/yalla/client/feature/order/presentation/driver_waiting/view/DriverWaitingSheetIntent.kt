package uz.yalla.client.feature.order.presentation.driver_waiting.view

import androidx.compose.ui.unit.Dp

sealed interface DriverWaitingSheetIntent {
    data class OnCancelled(val orderId: Int?) : DriverWaitingSheetIntent
    data object AddNewOrder : DriverWaitingSheetIntent
    data class SetHeaderHeight(val height: Dp) : DriverWaitingSheetIntent
    data class SetFooterHeight(val height: Dp) : DriverWaitingSheetIntent
}