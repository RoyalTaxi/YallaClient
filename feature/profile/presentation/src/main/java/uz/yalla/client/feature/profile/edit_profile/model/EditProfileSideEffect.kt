package uz.yalla.client.feature.profile.edit_profile.model

sealed interface EditProfileSideEffect {
    data object NavigateBack : EditProfileSideEffect
    data object NavigateToLogin : EditProfileSideEffect
    data object ShowImagePicker : EditProfileSideEffect
    data object ShowDeleteConfirmation : EditProfileSideEffect
    data object ClearFocus : EditProfileSideEffect
    data class ShowErrorMessage(val message: String) : EditProfileSideEffect
}
