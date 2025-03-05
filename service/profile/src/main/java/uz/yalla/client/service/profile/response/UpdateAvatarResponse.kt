package uz.yalla.client.service.profile.response

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAvatarResponse(
    val path: String?,
    val image: String?
)