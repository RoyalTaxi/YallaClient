package uz.yalla.client.feature.android.address_module.address.model

import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressType

internal data class AddressUIState(
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