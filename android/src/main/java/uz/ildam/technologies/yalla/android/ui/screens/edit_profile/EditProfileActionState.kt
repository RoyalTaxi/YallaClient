package uz.ildam.technologies.yalla.android.ui.screens.edit_profile

sealed interface EditProfileActionState {
    data object Loading : EditProfileActionState
    data object Error : EditProfileActionState
    data object GetSuccess : EditProfileActionState
    data object UpdateSuccess : EditProfileActionState
    data object UpdateAvatarSuccess : EditProfileActionState
}