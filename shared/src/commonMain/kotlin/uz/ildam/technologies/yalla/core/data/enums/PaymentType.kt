package uz.ildam.technologies.yalla.core.data.enums

sealed class PaymentType(val typeName: String) {
    data object CASH : PaymentType("cash")
    data class CARD(val cardId: String) : PaymentType("card")

    companion object {
        fun fromTypeName(typeName: String, cardNumber: String? = null): PaymentType {
            return when (typeName) {
                CASH.typeName -> CASH
                CARD("placeholder").typeName -> CARD(cardNumber ?: "")
                else -> CASH
            }
        }
    }
}