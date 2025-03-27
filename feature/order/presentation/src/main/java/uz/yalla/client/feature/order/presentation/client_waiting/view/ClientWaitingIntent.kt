package uz.yalla.client.feature.order.presentation.client_waiting.view

import androidx.compose.ui.unit.Dp
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetIntent

sealed interface ClientWaitingIntent {
    data class SetSheetHeight(val height: Dp) : ClientWaitingIntent
}