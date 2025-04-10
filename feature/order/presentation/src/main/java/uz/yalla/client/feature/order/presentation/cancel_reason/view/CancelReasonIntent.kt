package uz.yalla.client.feature.order.presentation.cancel_reason.view

import uz.yalla.client.feature.order.domain.model.response.order.SettingModel

sealed interface CancelReasonIntent {
    data object OnSelected : CancelReasonIntent
    data object NavigateBack : CancelReasonIntent
    data class OnSelect(val reason: SettingModel.CancelReason) : CancelReasonIntent
}