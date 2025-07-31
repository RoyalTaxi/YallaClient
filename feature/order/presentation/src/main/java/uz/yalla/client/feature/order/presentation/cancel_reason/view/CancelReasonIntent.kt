package uz.yalla.client.feature.order.presentation.cancel_reason.view

sealed interface CancelReasonIntent {
    data object NavigateBack : CancelReasonIntent
}