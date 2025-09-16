package uz.yalla.client.feature.history.history_details.intent

import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.domain.model.OrderHistoryModel

data class HistoryDetailsState(
    val orderDetails: OrderHistoryModel?,
    val route: List<MapPoint>,
) {
    companion object {
        val INITIAL = HistoryDetailsState(
            orderDetails = null,
            route = emptyList()
        )
    }
}