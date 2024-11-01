package uz.ildam.technologies.yalla.android.ui.screens.map

sealed interface MapActionState {
    data object LoadingAddressId : MapActionState
    data class AddressIdLoaded(val id: Int) : MapActionState
    data object LoadingAddressName : MapActionState
    data class AddressNameLoaded(val name: String?) : MapActionState
}