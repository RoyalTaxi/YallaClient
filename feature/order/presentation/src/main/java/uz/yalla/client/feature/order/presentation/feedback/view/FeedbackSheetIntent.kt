package uz.yalla.client.feature.order.presentation.feedback.view

sealed interface FeedbackSheetIntent {
    data object OnCompleteOrder: FeedbackSheetIntent
}