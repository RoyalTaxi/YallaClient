package uz.yalla.client.core.domain.model.type

enum class ThemeType {
    LIGHT,
    DARK,
    SYSTEM;

    companion object {
        fun fromValue(value: String): ThemeType {
            return when (value) {
                LIGHT.name -> LIGHT
                DARK.name -> DARK
                else -> SYSTEM
            }
        }
    }
}