package uz.yalla.client.feature.profile.edit_profile.view

import android.content.Context
import android.net.Uri
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import org.threeten.bp.LocalDate
import uz.yalla.client.feature.profile.edit_profile.components.Gender

internal sealed interface EditProfileIntent {
    data object NavigateBack : EditProfileIntent
    data object DeleteProfile : EditProfileIntent
    data object ConfirmDeleteProfile : EditProfileIntent
    data object UpdateImage : EditProfileIntent
    data object SaveProfile : EditProfileIntent
    data object OpenDateBottomSheet : EditProfileIntent
    data object CloseDateBottomSheet : EditProfileIntent
    data object LoadProfile : EditProfileIntent
    data class ChangeName(val name: String) : EditProfileIntent
    data class ChangeSurname(val surname: String) : EditProfileIntent
    data class ChangeGender(val gender: Gender) : EditProfileIntent
    data class ChangeBirthday(val birthday: LocalDate) : EditProfileIntent
    data class SetNewImage(val uri: Uri, val context: Context) : EditProfileIntent
}
