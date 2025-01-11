package uz.ildam.technologies.yalla.android.ui.screens.edit_profile

import org.threeten.bp.LocalDate

sealed interface EditProfileIntent {
    data object OnNavigateBack : EditProfileIntent
    data object OnUpdateImage : EditProfileIntent
    data object OnSave : EditProfileIntent
    data object OpenDateBottomSheet : EditProfileIntent
    data object CloseDateBottomSheet : EditProfileIntent
    data class OnChangeName(val name: String) : EditProfileIntent
    data class OnChangeSurname(val surname: String) : EditProfileIntent
    data class OnChangeGender(val gender: Gender) : EditProfileIntent
    data class OnChangeBirthday(val birthday: LocalDate) : EditProfileIntent
}