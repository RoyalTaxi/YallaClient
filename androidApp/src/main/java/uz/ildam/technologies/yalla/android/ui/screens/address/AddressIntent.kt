package uz.ildam.technologies.yalla.android.ui.screens.address

sealed interface AddressIntent {
    data object OnNavigateBack : AddressIntent
    data object OnSave : AddressIntent
    data class OnDelete(val id: Int) : AddressIntent
    data class OnChangeName(val value: String) : AddressIntent
    data class OnChangeApartment(val value: String) : AddressIntent
    data class OnChangeEntrance(val value: String) : AddressIntent
    data class OnChangeFloor(val value: String) : AddressIntent
    data class OnChangeComment(val value: String) : AddressIntent
    data class OnAddressSelected(val address: AddressUIState.Location) : AddressIntent
}