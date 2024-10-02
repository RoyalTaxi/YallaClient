package uz.ildam.technologies.yalla.feature.auth.data.request

import kotlinx.serialization.Serializable

@Serializable
data class SendAuthCodeRequest(
    val phone: String
)
