package uz.yalla.client.feature.info.about_app.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.feature.info.about_app.intent.AboutAppSideEffect
import uz.yalla.client.feature.info.about_app.intent.AboutAppState
import uz.yalla.client.feature.setting.domain.usecase.GetConfigUseCase

class AboutAppViewModel(
    internal val getConfigUseCase: GetConfigUseCase,
    internal val prefs: AppPreferences
) : BaseViewModel(), LifeCycleAware, ContainerHost<AboutAppState, AboutAppSideEffect> {

    override val container: Container<AboutAppState, AboutAppSideEffect> =
        container(AboutAppState.INITIAL)

    override var scope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch { getConfig() }
    }

    override fun onStart() {
        super.onStart()
        scope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())
    }
}