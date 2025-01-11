package uz.ildam.technologies.yalla.android.ui.screens.address

sealed interface AddressActionState {
    data object Loading : AddressActionState
    data class Error(val errorMessage: String) : AddressActionState
    data object GetSuccess : AddressActionState
    data object PutSuccess : AddressActionState
    data object DeleteSuccess : AddressActionState
}