package uz.yalla.client.feature.profile.edit_profile.intent

import org.threeten.bp.LocalDate
import uz.yalla.client.feature.profile.edit_profile.components.Gender

data class EditProfileState(
    val name: String,
    val surname: String,
    val phone: String,
    val gender: Gender,
    val newImage: ByteArray?,
    val birthday: LocalDate?,
    val imageUrl: String,
    val isDatePickerVisible: Boolean,
    val newImageUrl: String?,

    val originalName: String,
    val originalSurname: String,
    val originalBirthday: LocalDate?,
    val originalGender: Gender
) {
    val hasChanges: Boolean
        get() = name != originalName ||
                surname != originalSurname ||
                birthday != originalBirthday ||
                gender != originalGender ||
                newImage != null

    companion object {
        val INITIAL = EditProfileState(
            name = "",
            surname = "",
            phone = "",
            gender = Gender.NotSelected,
            newImage = null,
            birthday = null,
            imageUrl = "",
            isDatePickerVisible = false,
            newImageUrl = null,
            originalName = "",
            originalSurname = "",
            originalBirthday = null,
            originalGender = Gender.NotSelected
        )
    }
}