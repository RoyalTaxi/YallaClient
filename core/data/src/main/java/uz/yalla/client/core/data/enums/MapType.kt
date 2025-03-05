package uz.yalla.client.core.data.enums

enum class MapType(val typeName: String) {
    Google("google"),
    Gis("gis");

    companion object {
        fun fromTypeName(typeName: String): MapType {
            return entries.firstOrNull { it.typeName == typeName } ?: Google
        }
    }
}
