package uz.yalla.client.feature.android.profile.edit_profile.view

import org.threeten.bp.LocalDate
import uz.yalla.client.feature.android.profile.edit_profile.components.Gender

internal sealed interface EditProfileIntent {
    data object OnNavigateBack : EditProfileIntent
    data object OnUpdateImage : EditProfileIntent
    data object OnSave : EditProfileIntent
    data object OpenDateBottomSheet : EditProfileIntent
    data object CloseDateBottomSheet : EditProfileIntent
    data class OnChangeName(val name: String) : EditProfileIntent
    data class OnChangeSurname(val surname: String) : EditProfileIntent
    data class OnChangeGender(val gender: Gender) : EditProfileIntent
    data class OnChangeBirthday(val birthday: LocalDate) : EditProfileIntent
}