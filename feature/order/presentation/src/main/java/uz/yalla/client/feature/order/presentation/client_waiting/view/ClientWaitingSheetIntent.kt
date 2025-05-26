package uz.yalla.client.feature.order.presentation.client_waiting.view

import androidx.compose.ui.unit.Dp
import uz.yalla.client.core.domain.model.MapPoint

sealed interface ClientWaitingSheetIntent {
    data class UpdateRoute(val route: List<MapPoint>) : ClientWaitingSheetIntent
    data class OnCancelled(val orderId: Int?) : ClientWaitingSheetIntent
    data object AddNewOrder : ClientWaitingSheetIntent
    data class SetHeaderHeight(val height: Dp) : ClientWaitingSheetIntent
    data class SetFooterHeight(val height: Dp) : ClientWaitingSheetIntent
}