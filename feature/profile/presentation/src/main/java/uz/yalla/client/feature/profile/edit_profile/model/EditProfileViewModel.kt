package uz.yalla.client.feature.profile.edit_profile.model

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import uz.yalla.client.core.common.formation.formatWithDotsDMY
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.feature.profile.domain.model.request.UpdateMeDto
import uz.yalla.client.feature.profile.domain.usecase.GetMeUseCase
import uz.yalla.client.feature.profile.domain.usecase.LogoutUseCase
import uz.yalla.client.feature.profile.domain.usecase.UpdateAvatarUseCase
import uz.yalla.client.feature.profile.domain.usecase.UpdateMeUseCase
import uz.yalla.client.feature.profile.edit_profile.components.Gender
import uz.yalla.client.feature.profile.edit_profile.components.uriToByteArray
import java.util.Locale

internal sealed class NavigationEvent {
    data object NavigateBack : NavigationEvent()
    data object NavigateToStart : NavigationEvent()
}

internal class EditProfileViewModel(
    private val updateMeUseCase: UpdateMeUseCase,
    private val getMeUseCase: GetMeUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val prefs: AppPreferences
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUIState())
    val uiState = _uiState.asStateFlow()

    private val _navigationChannel = Channel<NavigationEvent>(Channel.CONFLATED)
    val navigationChannel = _navigationChannel.receiveAsFlow()

    fun getMe() = viewModelScope.launchWithLoading {
        getMeUseCase().onSuccess { result ->
            _uiState.update {
                it.copy(
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

    fun postMe() = viewModelScope.launchWithLoading {
        with(uiState.value) {
            updateMeUseCase(
                UpdateMeDto(
                    givenNames = name,
                    surname = surname,
                    birthday = birthday.formatWithDotsDMY(),
                    gender = gender.type,
                    image = newImageUrl
                )
            ).onSuccess {
                _uiState.update { currentState ->
                    currentState.copy(
                        originalName = currentState.name,
                        originalSurname = currentState.surname,
                        originalBirthday = currentState.birthday,
                        originalGender = currentState.gender,
                        newImage = null
                    )
                }
                _navigationChannel.trySend(
                    NavigationEvent.NavigateBack
                )
            }.onFailure(::handleException)
        }
    }

    fun updateAvatar() = viewModelScope.launchWithLoading {
        uiState.value.newImage?.let { newImage ->
            updateAvatarUseCase(newImage).onSuccess { result ->
                _uiState.update { it.copy(newImageUrl = result.image) }
                postMe()
            }.onFailure(::handleException)
        }
    }

    fun setNewImage(uri: Uri, context: Context) = viewModelScope.launchWithLoading {
        runCatching {
            context.uriToByteArray(uri)?.let { bytes ->
                _uiState.update { it.copy(newImage = bytes) }
            }
        }.onFailure(::handleException)
    }

    private fun parseBirthdayOrNull(birthdayStr: String?): LocalDate? {
        if (birthdayStr.isNullOrBlank()) return null
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
        return try {
            LocalDate.parse(birthdayStr, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    fun setDatePickerVisible(visible: Boolean) {
        _uiState.update { it.copy(isDatePickerVisible = visible) }
    }

    fun changeBirthday(localDate: LocalDate) {
        _uiState.update { it.copy(birthday = localDate) }
    }

    fun changeName(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun changeSurname(newSurname: String) {
        _uiState.update { it.copy(surname = newSurname) }
    }

    fun changeGender(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun logout() = viewModelScope.launchWithLoading {
        logoutUseCase().onSuccess {
            prefs.clearAll()
            _navigationChannel.trySend(
                NavigationEvent.NavigateToStart
            )
        }.onFailure(::handleException)
    }
}