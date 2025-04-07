package uz.yalla.client.service.map.response

import kotlinx.serialization.Serializable

@Serializable
data class SecondaryAddressResponseItem(
    val distance: Double?,
    val lat: Double?,
    val lng: Double?,
    val address_name: String?,
    val name: String?,
    val type: String?
)