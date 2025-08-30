package uz.yalla.client.feature.intro.language.model

import kotlinx.coroutines.CoroutineScope
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.feature.intro.language.intent.LanguageSideEffect
import uz.yalla.client.feature.intro.language.intent.LanguageState

internal class LanguageViewModel(
    internal val prefs: AppPreferences
) : BaseViewModel(), LifeCycleAware, ContainerHost<LanguageState, LanguageSideEffect> {

    override val container: Container<LanguageState, LanguageSideEffect> =
        container(LanguageState.INTERNAL)

    override val scope: CoroutineScope? = null
}