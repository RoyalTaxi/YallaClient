package uz.yalla.client.feature.profile.edit_profile.model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import uz.yalla.client.feature.profile.edit_profile.components.Gender
import uz.yalla.client.feature.profile.edit_profile.components.uriToByteArray

fun EditProfileViewModel.changeName(newName: String) = intent {
    reduce {
        state.copy(name = newName)
    }
}

fun EditProfileViewModel.changeSurname(newSurname: String) = intent {
    reduce {
        state.copy(surname = newSurname)
    }
}

fun EditProfileViewModel.changeGender(gender: Gender) = intent {
    reduce {
        state.copy(gender = gender)
    }
}

fun EditProfileViewModel.changeBirthday(localDate: LocalDate) = intent {
    reduce {
        state.copy(birthday = localDate)
    }
}

fun EditProfileViewModel.setDatePickerVisible(visible: Boolean) = intent {
    reduce {
        state.copy(isDatePickerVisible = visible)
    }
}

fun EditProfileViewModel.setNewImage(uri: Uri, context: Context) = intent {
    viewModelScope.launch {
        runCatching {
            context.uriToByteArray(uri)?.let { bytes ->
                reduce {
                    state.copy(newImage = bytes)
                }
            }
        }.onFailure(::handleException)
    }
}