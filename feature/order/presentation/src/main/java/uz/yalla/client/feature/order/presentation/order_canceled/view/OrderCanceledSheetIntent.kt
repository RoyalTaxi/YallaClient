package uz.yalla.client.feature.order.presentation.order_canceled.view

import androidx.compose.ui.unit.Dp

sealed interface OrderCanceledSheetIntent {
    data object StartNewOrder: OrderCanceledSheetIntent
    data class SetSheetHeight(val height: Dp) : OrderCanceledSheetIntent
}