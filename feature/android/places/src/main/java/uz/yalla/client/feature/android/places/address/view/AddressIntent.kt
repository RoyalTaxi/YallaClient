package uz.yalla.client.feature.android.places.address.view

internal sealed interface AddressIntent {
    data object OnNavigateBack : AddressIntent
    data object OnSave : AddressIntent
    data class OnDelete(val id: Int) : AddressIntent
    data class OnChangeName(val value: String) : AddressIntent
    data class OnChangeApartment(val value: String) : AddressIntent
    data class OnChangeEntrance(val value: String) : AddressIntent
    data class OnChangeFloor(val value: String) : AddressIntent
    data class OnChangeComment(val value: String) : AddressIntent
    data object OpenSearchSheet : AddressIntent
}