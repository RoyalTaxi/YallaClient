package uz.ildam.technologies.yalla.feature.payment.domain.model

data class AddCardModel(
    val expiry: String,
    val key: String,
    val number: String,
    val phone: String
)