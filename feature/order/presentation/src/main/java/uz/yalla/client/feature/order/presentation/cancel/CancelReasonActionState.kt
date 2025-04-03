package uz.yalla.client.feature.order.presentation.cancel

sealed class CancelReasonActionState {
    data object Loading : CancelReasonActionState()
    data object GettingSuccess : CancelReasonActionState()
    data object SettingSuccess : CancelReasonActionState()
    data class Error(val message: String? = null) : CancelReasonActionState()
}