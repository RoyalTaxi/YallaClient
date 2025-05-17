package uz.yalla.client.feature.order.domain.model.response.tarrif

sealed class AwardPaymentType(val typeName: String) {
    data object CASH : AwardPaymentType("cash")
    data object PERCENT : AwardPaymentType("percentage")

    companion object {
        fun fromTypeName(typeName: String): AwardPaymentType {
            return when (typeName) {
                CASH.typeName -> CASH
                PERCENT.typeName -> PERCENT

                else -> CASH
            }
        }
    }
}