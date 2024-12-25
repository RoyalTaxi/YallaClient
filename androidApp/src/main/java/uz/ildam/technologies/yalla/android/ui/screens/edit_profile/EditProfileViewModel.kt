package uz.ildam.technologies.yalla.android.ui.screens.edit_profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import uz.ildam.technologies.yalla.android.utils.formatWithDotsDMY
import uz.ildam.technologies.yalla.android.utils.uriToByteArray
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.profile.domain.model.request.UpdateMeDto
import uz.ildam.technologies.yalla.feature.profile.domain.usecase.GetMeUseCase
import uz.ildam.technologies.yalla.feature.profile.domain.usecase.UpdateAvatarUseCase
import uz.ildam.technologies.yalla.feature.profile.domain.usecase.UpdateMeUseCase
import java.util.Locale

class EditProfileViewModel(
    private val updateMeUseCase: UpdateMeUseCase,
    private val getMeUseCase: GetMeUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<EditProfileActionState>()
    val actionState = _actionState.asSharedFlow()

    fun getMe() = viewModelScope.launch {
        _actionState.emit(EditProfileActionState.Loading)
        when (val result = getMeUseCase()) {
            is Result.Error -> {
                _actionState.emit(EditProfileActionState.Error)
            }
            is Result.Success -> {
                _actionState.emit(EditProfileActionState.GetSuccess)

                // Update our UI state with the fetched data
                _uiState.update {
                    it.copy(
                        name = result.data.client.givenNames,
                        surname = result.data.client.surname,
                        gender = Gender.fromType(result.data.client.gender),
                        imageUrl = result.data.client.image,
                        birthday = parseBirthdayOrNull(result.data.client.birthday)
                    )
                }
            }
        }
    }

    /**
     * Update the user profile data excluding avatar (which is handled separately).
     */
    fun postMe() = viewModelScope.launch {
        _actionState.emit(EditProfileActionState.Loading)
        with(uiState.value) {
            val updateResult = updateMeUseCase(
                UpdateMeDto(
                    givenNames = name,
                    surname = surname,
                    birthday = birthday.formatWithDotsDMY(),
                    gender = gender.type,
                    image = newImageUrl
                )
            )

            when (updateResult) {
                is Result.Error -> _actionState.emit(EditProfileActionState.Error)
                is Result.Success -> _actionState.emit(EditProfileActionState.UpdateSuccess)
            }
        }
    }

    /**
     * Update the user's avatar only.
     */
    fun updateAvatar() = viewModelScope.launch {
        _actionState.emit(EditProfileActionState.Loading)
        uiState.value.newImage?.let { newImage ->
            when (val result = updateAvatarUseCase(newImage)) {
                is Result.Error -> {
                    _actionState.emit(EditProfileActionState.Error)
                }
                is Result.Success -> {
                    // Update avatar URL in the UI state
                    _uiState.update { it.copy(newImageUrl = result.data.image) }
                    _actionState.emit(EditProfileActionState.UpdateAvatarSuccess)
                }
            }
        }
    }

    /**
     * Handle new avatar selection from Image Picker.
     */
    fun setNewImage(uri: Uri, context: Context) = viewModelScope.launch {
        val byteArray = context.uriToByteArray(uri)
        if (byteArray != null) {
            _uiState.update { it.copy(newImage = byteArray) }
        } else {
            // If we failed to retrieve the byteArray from the URI
            _actionState.emit(EditProfileActionState.Error)
        }
    }

    /**
     * Called to parse a birthday string "dd.MM.yyyy" -> LocalDate?
     */
    fun parseBirthdayOrNull(birthdayStr: String?): LocalDate? {
        if (birthdayStr.isNullOrBlank()) return null

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
        return try {
            LocalDate.parse(birthdayStr, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    /**
     * Toggle or set the bottom sheet visibility for the DatePicker.
     */
    fun setDatePickerVisible(visible: Boolean) {
        _uiState.update { it.copy(isDatePickerVisible = visible) }
    }

    /**
     * Update the birthday in UI state.
     */
    fun changeBirthday(localDate: LocalDate) {
        _uiState.update { it.copy(birthday = localDate) }
    }

    /**
     * Update the user's name in UI state.
     */
    fun changeName(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    /**
     * Update the user's surname in UI state.
     */
    fun changeSurname(newSurname: String) {
        _uiState.update { it.copy(surname = newSurname) }
    }

    /**
     * Update the user's gender in UI state.
     */
    fun changeGender(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }
}