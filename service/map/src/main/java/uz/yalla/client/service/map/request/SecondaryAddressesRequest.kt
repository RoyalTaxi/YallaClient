package uz.yalla.client.service.map.request

import kotlinx.serialization.Serializable

@Serializable
data class SecondaryAddressesRequest(
    val lat: Double,
    val lng: Double
)