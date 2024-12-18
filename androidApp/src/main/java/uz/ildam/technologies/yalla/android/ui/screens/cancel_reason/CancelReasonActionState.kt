package uz.ildam.technologies.yalla.android.ui.screens.cancel_reason

sealed interface CancelReasonActionState {
    data object Loading : CancelReasonActionState
    data object Error : CancelReasonActionState
    data object GettingSuccess : CancelReasonActionState
    data object SettingSuccess : CancelReasonActionState
}