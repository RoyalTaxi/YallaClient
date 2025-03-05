package uz.yalla.client.service.map.response

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