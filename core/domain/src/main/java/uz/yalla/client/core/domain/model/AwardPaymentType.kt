package uz.yalla.client.core.domain.model

sealed class AwardPaymentType(val typeName: String) {
    data object BALANCE : AwardPaymentType("balance")
    data object PAYNET : AwardPaymentType("paynet")

    companion object {
        fun fromTypeName(typeName: String): AwardPaymentType {
            return when (typeName) {
                BALANCE.typeName -> BALANCE
                PAYNET.typeName -> PAYNET

                else -> BALANCE
            }
        }
    }
}