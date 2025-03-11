package uz.yalla.client.feature.order.presentation.cancel

sealed interface CancelReasonActionState {
    data object Loading : CancelReasonActionState
    data object Error : CancelReasonActionState
    data object GettingSuccess : CancelReasonActionState
    data object SettingSuccess : CancelReasonActionState
}