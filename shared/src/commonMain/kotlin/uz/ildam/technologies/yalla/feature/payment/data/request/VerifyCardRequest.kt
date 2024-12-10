package uz.ildam.technologies.yalla.feature.payment.data.request

import kotlinx.serialization.Serializable

@Serializable
data class VerifyCardRequest(
    val key: String,
    val confirm_code: String
)