package uz.yalla.client.service.places.response

import kotlinx.serialization.Serializable

@Serializable
data class PlaceRemoteModel(
    val id: Int?,
    val name: String?,
    val address: String?,
    val coords: Coords?,
    val type: String?,
    val enter: String?,
    val apartment: String?,
    val floor: String?,
    val comment: String?,
    val created_at: String?
) {
    @Serializable
    data class Coords(
        val lat: Double?,
        val lng: Double?
    )
}