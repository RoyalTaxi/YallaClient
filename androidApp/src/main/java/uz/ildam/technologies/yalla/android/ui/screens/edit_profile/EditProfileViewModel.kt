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
            is Result.Error -> _actionState.emit(EditProfileActionState.Error)
            is Result.Success -> {
                _actionState.emit(EditProfileActionState.GetSuccess)
                _uiState.update {
                    it.copy(
                        name = result.data.client.givenNames,
                        surname = result.data.client.surname,
                        gender = result.data.client.gender,
                        imageUrl = result.data.client.image,
                        birthday = parseBirthdayOrNull(result.data.client.birthday)
                    )
                }
            }
        }
    }

    fun postMe() = viewModelScope.launch {
        _actionState.emit(EditProfileActionState.Loading)
        with(uiState.value) {
            when (updateMeUseCase(
                UpdateMeDto(
                    givenNames = name,
                    surname = surname,
                    birthday = birthday.formatWithDotsDMY(),
                    gender = gender,
                    image = imageUrl
                )
            )) {
                is Result.Error -> _actionState.emit(EditProfileActionState.Error)
                is Result.Success -> _actionState.emit(EditProfileActionState.UpdateSuccess)
            }
        }
    }

    fun updateAvatar() = viewModelScope.launch {
        _actionState.emit(EditProfileActionState.Loading)
        uiState.value.newImage?.let { newImage ->
            when (val result = updateAvatarUseCase(newImage)) {
                is Result.Error -> _actionState.emit(EditProfileActionState.Error)
                is Result.Success -> {
                    _uiState.update { it.copy(imageUrl = result.data.image) }

                    _actionState.emit(EditProfileActionState.UpdateAvatarSuccess)
                }
            }
        }
    }

    fun setNewImage(uri: Uri, context: Context) = viewModelScope.launch {
        val byteArray = context.uriToByteArray(uri)
        if (byteArray != null) {
            _uiState.update { it.copy(newImage = byteArray) }
        } else {
            _actionState.emit(EditProfileActionState.Error)
        }
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
}