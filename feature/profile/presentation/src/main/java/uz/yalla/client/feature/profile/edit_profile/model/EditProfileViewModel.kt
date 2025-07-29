package uz.yalla.client.feature.profile.edit_profile.model

import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.feature.profile.domain.usecase.GetMeUseCase
import uz.yalla.client.feature.profile.domain.usecase.LogoutUseCase
import uz.yalla.client.feature.profile.domain.usecase.UpdateAvatarUseCase
import uz.yalla.client.feature.profile.domain.usecase.UpdateMeUseCase

class EditProfileViewModel(
    internal val updateMeUseCase: UpdateMeUseCase,
    internal val getMeUseCase: GetMeUseCase,
    internal val updateAvatarUseCase: UpdateAvatarUseCase,
    internal val logoutUseCase: LogoutUseCase,
    internal val appPreferences: AppPreferences,
    internal val staticPreferences: StaticPreferences
) : ContainerHost<EditProfileUIState, EditProfileSideEffect>, BaseViewModel() {
    override val container = container<EditProfileUIState, EditProfileSideEffect>(EditProfileUIState())
}