package uz.yalla.client.feature.profile.edit_profile.components

sealed class Gender(val type: String) {
    data object Male : Gender("MALE")
    data object Female : Gender("FEMALE")
    data object NotSelected : Gender("NOT_SELECTED")

    companion object {
        fun fromType(value: String): Gender {
            return when (value.uppercase()) {
                Male.type -> Male
                Female.type -> Female
                else -> NotSelected
            }
        }
    }
}