package uz.ildam.technologies.yalla.feature.addresses.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PostOneAddressDto(
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val type: String,
    val enter: String,
    val apartment: String,
    val floor: String,
    val comment: String
)