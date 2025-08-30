package uz.yalla.client.feature.profile.edit_profile.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.feature.profile.domain.usecase.GetMeUseCase
import uz.yalla.client.feature.profile.domain.usecase.LogoutUseCase
import uz.yalla.client.feature.profile.domain.usecase.UpdateAvatarUseCase
import uz.yalla.client.feature.profile.domain.usecase.UpdateMeUseCase
import uz.yalla.client.feature.profile.edit_profile.intent.EditProfileSideEffect
import uz.yalla.client.feature.profile.edit_profile.intent.EditProfileState

class EditProfileViewModel(
    internal val updateMeUseCase: UpdateMeUseCase,
    internal val getMeUseCase: GetMeUseCase,
    internal val updateAvatarUseCase: UpdateAvatarUseCase,
    internal val logoutUseCase: LogoutUseCase,
    internal val appPreferences: AppPreferences,
    internal val staticPreferences: StaticPreferences
) : BaseViewModel(), LifeCycleAware, ContainerHost<EditProfileState, EditProfileSideEffect> {

    override val container: Container<EditProfileState, EditProfileSideEffect> =
        container(EditProfileState.INITIAL)

    override var scope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch {
            getMe()
        }
    }

    override fun onStart() {
        super.onStart()
        scope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())
        scope?.launch {
            observerDatePickerVisibility()
        }
    }
}