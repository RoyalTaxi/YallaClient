package uz.yalla.client.feature.intro.language.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.lifecycle.MakeBridge
import uz.yalla.client.feature.intro.language.intent.LanguageSideEffect
import uz.yalla.client.feature.intro.language.model.LanguageViewModel
import uz.yalla.client.feature.intro.language.model.onIntent
import uz.yalla.client.feature.intro.language.model.setLanguage
import uz.yalla.client.feature.intro.language.navigation.FromLanguage

@Composable
internal fun LanguageRoute(
    navigateTo: (FromLanguage) -> Unit,
    viewModel: LanguageViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    lifecycleOwner.MakeBridge(viewModel)

    viewModel.collectSideEffect { effect ->
        when (effect) {
            LanguageSideEffect.NavigateOnboarding -> navigateTo(FromLanguage.NavigateOnboarding)
            is LanguageSideEffect.SetLanguage -> viewModel.setLanguage(
                language = effect.language,
                context = context
            )
        }
    }

    LanguageScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}