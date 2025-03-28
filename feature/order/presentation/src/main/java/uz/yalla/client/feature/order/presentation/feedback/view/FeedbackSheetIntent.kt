package uz.yalla.client.feature.order.presentation.feedback.view

import androidx.compose.ui.unit.Dp
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingIntent

sealed interface FeedbackSheetIntent {
    data class SetSheetHeight(val height: Dp) : FeedbackSheetIntent
    data object OnCompleteOrder: FeedbackSheetIntent
}