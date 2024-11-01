package uz.ildam.technologies.yalla.feature.map.data.response.address

import kotlinx.serialization.Serializable

@Serializable
data class AddressResponse(
    val lat: String?,
    val lng: String?,
    val name: String?
)