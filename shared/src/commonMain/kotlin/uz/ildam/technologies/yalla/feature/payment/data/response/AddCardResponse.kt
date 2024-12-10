package uz.ildam.technologies.yalla.feature.payment.data.response

import kotlinx.serialization.Serializable

@Serializable
data class AddCardResponse(
    val expiry: String?,
    val key: String?,
    val number: String?,
    val phone: String?
)