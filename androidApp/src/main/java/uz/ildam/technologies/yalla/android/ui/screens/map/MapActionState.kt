package uz.ildam.technologies.yalla.android.ui.screens.map

sealed interface MapActionState {
    data object LoadingAddressId : MapActionState
    data class AddressIdLoaded(val id: Long) : MapActionState
    data object LoadingAddressName : MapActionState
    data class AddressNameLoaded(val name: String?) : MapActionState
    data object LoadingPolygon : MapActionState
    data object PolygonLoaded : MapActionState
    data object LoadingTariffs : MapActionState
    data object TariffsLoaded : MapActionState
}