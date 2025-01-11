package uz.ildam.technologies.yalla.android.ui.screens.cancel_reason

import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SettingModel

data class CancelReasonUIState(
    val reasons: List<SettingModel.CancelReason> = emptyList(),
    val selectedReason: SettingModel.CancelReason? = null
)