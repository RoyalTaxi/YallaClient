package uz.yalla.client.service.places.request

import kotlinx.serialization.Serializable

@Serializable
data class PostOnePlaceRequest(
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