package uz.ildam.technologies.yalla.feature.auth.data.response

import kotlinx.serialization.Serializable

@Serializable
data class SendAuthCodeResponse(
    val code: Int?,
    val time: Int?,
    val result_message: String?
)