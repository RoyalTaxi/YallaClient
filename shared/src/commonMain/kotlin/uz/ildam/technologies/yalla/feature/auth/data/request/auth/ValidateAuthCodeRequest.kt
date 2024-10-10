package uz.ildam.technologies.yalla.feature.auth.data.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class ValidateAuthCodeRequest(
    val phone: String,
    val code: Int
)
