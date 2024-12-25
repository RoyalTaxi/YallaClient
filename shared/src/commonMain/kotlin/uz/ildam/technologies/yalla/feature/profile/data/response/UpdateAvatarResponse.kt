package uz.ildam.technologies.yalla.feature.profile.data.response

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAvatarResponse(
    val path: String?,
    val image: String?
)