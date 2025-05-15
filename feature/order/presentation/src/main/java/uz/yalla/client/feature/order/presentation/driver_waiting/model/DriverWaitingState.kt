package uz.yalla.client.feature.order.presentation.driver_waiting.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

data class DriverWaitingState (
    val orderId: Int? = null,
    val selectedDriver: ShowOrderModel? = null,
    val detailsBottomSheetVisibility: Boolean = false,
    val cancelBottomSheetVisibility: Boolean = false,
    val headerHeight: Dp = 0.dp,
    val footerHeight: Dp = 0.dp,
    val isOrderCancellable: Boolean = false
)