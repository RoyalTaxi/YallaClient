package uz.yalla.client.core.domain.model

enum class MapType(val typeName: String) {
    Google("google"),
    Gis("gis"),
    Libre("libre");

    companion object {
        fun fromTypeName(typeName: String): MapType {
            return entries.firstOrNull { it.typeName == typeName } ?: Google
        }
    }
}
