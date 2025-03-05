package uz.yalla.client.feature.payment.domain.model

data class CardListItemModel(
    val cardId: String,
    val default: Boolean,
    val expiry: String,
    val maskedPan: String
)