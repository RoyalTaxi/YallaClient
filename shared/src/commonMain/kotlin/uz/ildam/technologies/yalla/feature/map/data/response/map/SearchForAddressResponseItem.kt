package uz.ildam.technologies.yalla.feature.map.data.response.map

import kotlinx.serialization.Serializable

@Serializable
data class SearchForAddressResponseItem(
    val address_id: Int?,
    val address_name: String?,
    val db: Boolean?,
    val distance: Double?,
    val lat: Double?,
    val lng: Double?,
    val name: String?
)