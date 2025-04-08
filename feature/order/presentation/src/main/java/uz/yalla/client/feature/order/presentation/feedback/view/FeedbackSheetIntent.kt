package uz.yalla.client.feature.order.presentation.feedback.view

import androidx.compose.ui.unit.Dp

sealed interface FeedbackSheetIntent {
    data object OnCompleteOrder: FeedbackSheetIntent
    data class SetHeaderHeight(val height: Dp) : FeedbackSheetIntent
    data class SetFooterHeight(val height: Dp) : FeedbackSheetIntent
}