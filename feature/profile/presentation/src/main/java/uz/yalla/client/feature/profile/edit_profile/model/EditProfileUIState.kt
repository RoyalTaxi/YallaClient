package uz.yalla.client.feature.profile.edit_profile.model

import org.threeten.bp.LocalDate
import uz.yalla.client.feature.profile.edit_profile.components.Gender

internal data class EditProfileUIState(
    val name: String = "",
    val surname: String = "",
    val gender: Gender = Gender.NotSelected,
    val newImage: ByteArray? = null,
    val birthday: LocalDate? = null,
    val imageUrl: String = "",
    val isDatePickerVisible: Boolean = false,
    val newImageUrl: String? = null
)