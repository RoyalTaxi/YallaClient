package uz.yalla.client.feature.profile.edit_profile.model

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.formation.formatWithDotsDMY
import uz.yalla.client.feature.profile.domain.model.request.UpdateMeDto
import uz.yalla.client.feature.profile.edit_profile.components.Gender

fun EditProfileViewModel.getMe() = intent {
    viewModelScope.launch {
        getMeUseCase().onSuccess { result ->
            reduce {
                state.copy(
                    name = result.client.givenNames,
                    surname = result.client.surname,
                    gender = Gender.fromType(result.client.gender),
                    imageUrl = result.client.image,
                    birthday = parseBirthdayOrNull(result.client.birthday),
                    phone = result.client.phone,

                    originalName = result.client.givenNames,
                    originalSurname = result.client.surname,
                    originalGender = Gender.fromType(result.client.gender),
                    originalBirthday = parseBirthdayOrNull(result.client.birthday)
                )
            }
        }.onFailure(::handleException)
    }
}

fun EditProfileViewModel.postMe() = intent {
    viewModelScope.launch {
        with(state) {
            updateMeUseCase(
                UpdateMeDto(
                    givenNames = name,
                    surname = surname,
                    birthday = birthday.formatWithDotsDMY(),
                    gender = gender.type,
                    image = newImageUrl
                )
            ).onSuccess {
                reduce {
                    state.copy(
                        originalName = state.name,
                        originalSurname = state.surname,
                        originalBirthday = state.birthday,
                        originalGender = state.gender,
                        newImage = null
                    )
                }
                postSideEffect(uz.yalla.client.feature.profile.edit_profile.intent.EditProfileSideEffect.NavigateBack)
            }.onFailure(::handleException)
        }
    }
}

fun EditProfileViewModel.updateAvatar() = intent {
    viewModelScope.launch {
        state.newImage?.let { newImage ->
            updateAvatarUseCase(newImage).onSuccess { result ->
                reduce {
                    state.copy(newImageUrl = result.image)
                }
                postMe()
            }.onFailure(::handleException)
        }
    }
}
