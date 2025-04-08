package uz.yalla.client.feature.order.presentation.feedback.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

data class FeedbackSheetState(
    val orderId: Int? = null,
    val order: ShowOrderModel? = null,
    val headerHeight: Dp = 0.dp,
    val footerHeight: Dp = 0.dp
)