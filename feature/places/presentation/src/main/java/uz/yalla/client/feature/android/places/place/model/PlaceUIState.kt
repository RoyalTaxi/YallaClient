package uz.yalla.client.feature.android.places.place.model

internal data class PlaceUIState(
    val addressType: AddressType = AddressType.OTHER,
    val selectedAddress: Location? = null,
    val addressName: String = "",
    val apartment: String = "",
    val entrance: String = "",
    val floor: String = "",
    val comment: String = ""
) {
    data class Location(
        val name: String = "",
        val lat: Double = 0.0,
        val lng: Double = 0.0
    )
}