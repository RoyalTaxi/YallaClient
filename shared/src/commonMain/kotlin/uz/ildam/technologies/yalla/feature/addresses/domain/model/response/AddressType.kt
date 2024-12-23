package uz.ildam.technologies.yalla.feature.addresses.domain.model.response

sealed class AddressType(val typeName: String) {
    data object OTHER : AddressType("other")
    data object HOME : AddressType("home")
    data object WORK : AddressType("work")

    companion object {
        fun fromType(typeName: String): AddressType {
            return when (typeName.lowercase()) {
                HOME.typeName -> HOME
                WORK.typeName -> WORK
                else -> OTHER
            }
        }
    }
}