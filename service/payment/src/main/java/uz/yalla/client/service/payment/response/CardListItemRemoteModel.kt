package uz.yalla.client.service.payment.response

import kotlinx.serialization.Serializable

@Serializable
data class CardListItemRemoteModel(
    val card_id: String?,
    val default: Boolean?,
    val expiry: String?,
    val masked_pan: String?
)