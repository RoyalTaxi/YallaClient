package uz.yalla.client.feature.profile.edit_profile.model

import uz.yalla.client.feature.profile.edit_profile.view.EditProfileIntent

internal fun EditProfileViewModel.onIntent(intent: EditProfileIntent) {
    when (intent) {
        is EditProfileIntent.CloseDateBottomSheet -> {
            setDatePickerVisible(false)
        }

        is EditProfileIntent.ChangeBirthday -> {
            changeBirthday(intent.birthday)
        }

        is EditProfileIntent.ChangeGender -> {
            changeGender(intent.gender)
        }

        is EditProfileIntent.ChangeName -> {
            changeName(intent.name)
        }

        is EditProfileIntent.ChangeSurname -> {
            changeSurname(intent.surname)
        }

        is EditProfileIntent.DeleteProfile -> {
            intent {
                postSideEffect(EditProfileSideEffect.ShowDeleteConfirmation)
            }
        }

        is EditProfileIntent.ConfirmDeleteProfile -> {
            logout()
        }

        is EditProfileIntent.LoadProfile -> {
            getMe()
        }

        is EditProfileIntent.NavigateBack -> {
            intent {
                postSideEffect(EditProfileSideEffect.NavigateBack)
            }
        }

        is EditProfileIntent.SaveProfile -> {
            intent {
                postSideEffect(EditProfileSideEffect.ClearFocus)
            }
            if (container.stateFlow.value.newImage != null) {
                updateAvatar()
            } else {
                postMe()
            }
        }

        is EditProfileIntent.SetNewImage -> {
            setNewImage(intent.uri, intent.context)
        }

        is EditProfileIntent.UpdateImage -> {
            intent {
                postSideEffect(EditProfileSideEffect.ShowImagePicker)
            }
        }

        is EditProfileIntent.OpenDateBottomSheet -> {
            setDatePickerVisible(true)
        }
    }
}
