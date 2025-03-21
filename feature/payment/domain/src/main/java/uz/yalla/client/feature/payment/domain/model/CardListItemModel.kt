package uz.yalla.client.feature.payment.domain.model

data class CardListItemModel(
    val cardId: String,
    val maskedPan: String
)