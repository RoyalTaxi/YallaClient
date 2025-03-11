package uz.yalla.client.feature.order.presentation.cancel

import uz.yalla.client.feature.order.domain.model.response.order.SettingModel


data class CancelReasonUIState(
    val reasons: List<SettingModel.CancelReason> = emptyList(),
    val selectedReason: SettingModel.CancelReason? = null
)