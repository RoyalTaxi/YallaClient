package uz.yalla.client.feature.order.presentation.on_ride.model

import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

data class OnTheRideSheetState (
    val order: ShowOrderModel.Executor? = null,
)

