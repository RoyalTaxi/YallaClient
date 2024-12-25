package uz.ildam.technologies.yalla.feature.profile.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAvatarModel(
    val path: String,
    val image: String
)