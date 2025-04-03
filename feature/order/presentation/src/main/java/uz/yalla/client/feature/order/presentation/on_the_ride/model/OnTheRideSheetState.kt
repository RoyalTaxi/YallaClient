package uz.yalla.client.feature.order.presentation.on_the_ride.model

import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

data class OnTheRideSheetState (
    val orderId: Int? = null,
    val selectedDriver: ShowOrderModel? = null,
    val detailsBottomSheetVisibility: Boolean = false,
)

