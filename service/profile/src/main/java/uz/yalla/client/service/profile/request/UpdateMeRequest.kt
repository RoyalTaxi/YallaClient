package uz.yalla.client.service.profile.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateMeRequest(
    val given_names: String,
    val sur_name: String,
    val birthday: String,
    val gender: String,
    val image: String?
)