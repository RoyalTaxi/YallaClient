package uz.ildam.technologies.yalla.android.ui.screens.edit_profile

import org.threeten.bp.LocalDate

data class EditProfileUIState(
    val name: String = "",
    val surname: String = "",
    val gender: String = "",
    val newImage: ByteArray? = null,
    val birthday: LocalDate? = null,
    val imageUrl: String = ""
)