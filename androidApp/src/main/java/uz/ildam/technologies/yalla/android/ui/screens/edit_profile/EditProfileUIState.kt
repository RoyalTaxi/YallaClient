package uz.ildam.technologies.yalla.android.ui.screens.edit_profile

import org.threeten.bp.LocalDate

data class EditProfileUIState(
    val name: String = "",
    val surname: String = "",
    val gender: Gender = Gender.NotSelected,
    val newImage: ByteArray? = null,
    val birthday: LocalDate? = null,
    val imageUrl: String = "",
    val isDatePickerVisible: Boolean = false,
    val newImageUrl: String? = null
)