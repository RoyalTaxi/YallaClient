package uz.ildam.technologies.yalla.core.data.enums

sealed class PaymentType(val typeName: String) {
    data object CASH : PaymentType("cash")
    data class CARD(
        val cardId: String,
        val cardNumber: String,
    ) : PaymentType("card")

    companion object {
        fun fromTypeName(
            typeName: String,
            cardId: String? = null,
            cardNumber: String? = null
        ): PaymentType {
            return when (typeName) {
                CASH.typeName -> CASH
                CARD(
                    "placeholder",
                    "placeholder"
                ).typeName -> CARD(
                    cardId ?: "",
                    cardNumber ?: ""
                )

                else -> CASH
            }
        }
    }
}