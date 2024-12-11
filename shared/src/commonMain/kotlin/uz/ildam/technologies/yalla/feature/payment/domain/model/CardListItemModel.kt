package uz.ildam.technologies.yalla.feature.payment.domain.model

data class CardListItemModel(
    val cardId: String,
    val default: Boolean,
    val expiry: String,
    val maskedPan: String
)