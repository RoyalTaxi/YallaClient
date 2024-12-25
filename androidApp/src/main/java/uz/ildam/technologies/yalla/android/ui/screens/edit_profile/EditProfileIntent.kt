package uz.ildam.technologies.yalla.android.ui.screens.edit_profile

sealed interface EditProfileIntent {
    data object OnNavigateBack : EditProfileIntent
    data object OnUpdateImage : EditProfileIntent
    data object OnSave : EditProfileIntent
    data class OnChangeName(val name: String) : EditProfileIntent
    data class OnChangeSurname(val surname: String) : EditProfileIntent
    data class OnChangeGender(val gender: String) : EditProfileIntent
}