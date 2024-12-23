package uz.ildam.technologies.yalla.android.ui.screens.address

sealed interface AddressActionState {
    data object Loading : AddressActionState
    data object Error : AddressActionState
    data object GetSuccess : AddressActionState
    data object PutSuccess : AddressActionState
    data object DeleteSuccess : AddressActionState
}