package uz.yalla.client.feature.profile.edit_profile.model

internal sealed interface EditProfileActionState {
    data object Loading : EditProfileActionState
    data object Error : EditProfileActionState
    data object GetSuccess : EditProfileActionState
    data object UpdateSuccess : EditProfileActionState
    data object UpdateAvatarSuccess : EditProfileActionState
    data object LogoutSuccess : EditProfileActionState
    data object LogoutError : EditProfileActionState
}