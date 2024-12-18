package uz.ildam.technologies.yalla.android.ui.screens.cancel_reason

import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SettingModel

sealed interface CancelReasonIntent {
    data object OnSelected : CancelReasonIntent
    data object NavigateBack : CancelReasonIntent
    data class OnSelect(val reason: SettingModel.CancelReason) : CancelReasonIntent
}