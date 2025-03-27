package uz.yalla.client.feature.order.presentation.driver_waiting.model

import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

data class DriverWaitingState (
    val selectedDriver: ShowOrderModel.Executor? = null
)