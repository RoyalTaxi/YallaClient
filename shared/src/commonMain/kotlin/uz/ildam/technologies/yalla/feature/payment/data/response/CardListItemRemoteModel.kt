package uz.ildam.technologies.yalla.feature.payment.data.response

import kotlinx.serialization.Serializable

@Serializable
data class CardListItemRemoteModel(
    val card_id: String?,
    val default: Boolean?,
    val expiry: String?,
    val masked_pan: String?
)