package uz.ildam.technologies.yalla.feature.payment.data.request

import kotlinx.serialization.Serializable

@Serializable
data class AddCardRequest(
    val number: String,
    val expiry: String
)