package uz.yalla.client.feature.order.presentation.client_waiting.model

import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

data class ClientWaitingState (
    val orderId: Int? = null,
    val selectedDriver: ShowOrderModel? = null,
    val detailsBottomSheetVisibility: Boolean = false,
    val cancelBottomSheetVisibility: Boolean = false
)