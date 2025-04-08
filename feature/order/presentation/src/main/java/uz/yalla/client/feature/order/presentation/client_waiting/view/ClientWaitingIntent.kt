package uz.yalla.client.feature.order.presentation.client_waiting.view

import androidx.compose.ui.unit.Dp
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingIntent
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetIntent

sealed interface ClientWaitingIntent {
    data class UpdateRoute(val route: List<MapPoint>) : ClientWaitingIntent
    data class OnCancelled(val orderId: Int?) : ClientWaitingIntent
    data object AddNewOrder : ClientWaitingIntent
    data class SetHeaderHeight(val height: Dp) : ClientWaitingIntent
    data class SetFooterHeight(val height: Dp) : ClientWaitingIntent
}