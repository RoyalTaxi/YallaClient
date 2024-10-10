package uz.ildam.technologies.yalla.feature.auth.data.response.auth

import kotlinx.serialization.Serializable

@Serializable
data class SendAuthCodeResponse(
    val time: Int?,
    val result_message: String?
)