package uz.ildam.technologies.yalla.android.ui.screens.addresses

sealed interface AddressesActionState {
    data object Loading : AddressesActionState
    data object Error : AddressesActionState
    data object Success : AddressesActionState
}