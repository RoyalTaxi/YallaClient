package uz.yalla.client.core.domain.model

sealed class PlaceType(val typeName: String) {
    data object OTHER : PlaceType("other")
    data object HOME : PlaceType("home")
    data object WORK : PlaceType("work")

    companion object {
        fun fromType(typeName: String): PlaceType {
            return when (typeName.lowercase()) {
                HOME.typeName -> HOME
                WORK.typeName -> WORK
                else -> OTHER
            }
        }
    }
}