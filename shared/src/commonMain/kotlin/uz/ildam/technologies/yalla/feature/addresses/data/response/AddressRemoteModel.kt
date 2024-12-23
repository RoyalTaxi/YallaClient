package uz.ildam.technologies.yalla.feature.addresses.data.response

import kotlinx.serialization.Serializable

@Serializable
data class AddressRemoteModel(
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