package uz.yalla.client.feature.setting.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.feature.setting.intent.SettingsSideEffect
import uz.yalla.client.feature.setting.intent.SettingsState

internal class SettingsViewModel(
    internal val prefs: AppPreferences
) : BaseViewModel(), LifeCycleAware, ContainerHost<SettingsState, SettingsSideEffect> {

    override val container: Container<SettingsState, SettingsSideEffect> =
        container(SettingsState.INITIAL)

    override var scope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch { }
    }

    override fun onStart() {
        super.onStart()
        scope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())
        scope?.launch { observeThemeChanges() }
        scope?.launch { observeLocaleChanges() }
    }
}