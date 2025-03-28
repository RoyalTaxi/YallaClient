package uz.yalla.client.feature.order.presentation.feedback.model

import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

data class FeedbackSheetState(
    val orderId: Int? = null,
    val order: ShowOrderModel? = null
)