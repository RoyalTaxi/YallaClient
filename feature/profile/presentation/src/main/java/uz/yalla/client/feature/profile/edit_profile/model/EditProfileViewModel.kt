package uz.yalla.client.feature.profile.edit_profile.model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import uz.yalla.client.core.common.formation.formatWithDotsDMY
import uz.yalla.client.feature.profile.edit_profile.components.Gender
import uz.yalla.client.feature.profile.edit_profile.components.uriToByteArray
import uz.yalla.client.feature.profile.domain.model.request.UpdateMeDto
import uz.yalla.client.feature.profile.domain.usecase.GetMeUseCase
import uz.yalla.client.feature.profile.domain.usecase.UpdateAvatarUseCase
import uz.yalla.client.feature.profile.domain.usecase.UpdateMeUseCase
import java.util.Locale

internal class EditProfileViewModel(
    private val updateMeUseCase: UpdateMeUseCase,
    private val getMeUseCase: GetMeUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<EditProfileActionState>()
    val actionState = _actionState.asSharedFlow()

    fun getMe() = viewModelScope.launch(Dispatchers.IO) {
        _actionState.emit(EditProfileActionState.Loading)
        getMeUseCase().onSuccess { result ->
            _actionState.emit(EditProfileActionState.GetSuccess)

            _uiState.update {
                it.copy(
                    name = result.client.givenNames,
                    surname = result.client.surname,
                    gender = Gender.fromType(result.client.gender),
                    imageUrl = result.client.image,
                    birthday = parseBirthdayOrNull(result.client.birthday)
                )
            }
        }
    }

    fun postMe() = viewModelScope.launch(Dispatchers.IO) {
        _actionState.emit(EditProfileActionState.Loading)
        with(uiState.value) {
            updateMeUseCase(
                UpdateMeDto(
                    givenNames = name,
                    surname = surname,
                    birthday = birthday.formatWithDotsDMY(),
                    gender = gender.type,
                    image = newImageUrl
                )
            ).onSuccess { _actionState.emit(EditProfileActionState.UpdateSuccess) }
                .onFailure { _actionState.emit(EditProfileActionState.Error) }
        }
    }

    fun updateAvatar() = viewModelScope.launch(Dispatchers.IO) {
        _actionState.emit(EditProfileActionState.Loading)
        uiState.value.newImage?.let { newImage ->
            updateAvatarUseCase(newImage).onSuccess { result ->
                _uiState.update { it.copy(newImageUrl = result.image) }
                _actionState.emit(EditProfileActionState.UpdateAvatarSuccess)
            }.onFailure { _actionState.emit(EditProfileActionState.Error) }
        }
    }

    fun setNewImage(uri: Uri, context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val byteArray = context.uriToByteArray(uri)
        if (byteArray != null) _uiState.update { it.copy(newImage = byteArray) }
        else _actionState.emit(EditProfileActionState.Error)
    }

    fun parseBirthdayOrNull(birthdayStr: String?): LocalDate? {
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
}