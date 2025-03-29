package uz.yalla.client.feature.order.presentation.order_canceled.view

sealed interface OrderCanceledSheetIntent {
    data object StartNewOrder: OrderCanceledSheetIntent
}