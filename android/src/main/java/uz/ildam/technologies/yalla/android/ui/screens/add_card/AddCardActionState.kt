package uz.ildam.technologies.yalla.android.ui.screens.add_card

sealed interface AddCardActionState {
    data object Loading : AddCardActionState
    data object Error : AddCardActionState
    data class Success(
        val key: String,
        val cardNumber: String,
        val cardExpiry: String
    ) : AddCardActionState
}