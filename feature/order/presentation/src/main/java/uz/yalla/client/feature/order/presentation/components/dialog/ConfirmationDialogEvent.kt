package uz.yalla.client.feature.order.presentation.components.dialog

sealed class ConfirmationDialogEvent {
    data object Invisible : ConfirmationDialogEvent()
    data object Success : ConfirmationDialogEvent()
    data object Error : ConfirmationDialogEvent()

}