package uz.yalla.client.feature.intro.language.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.feature.intro.language.model.LanguageViewModel

@Composable
internal fun LanguageRoute(
    onNext: () -> Unit,
    vm: LanguageViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LanguageScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is LanguageIntent.NavigateNext -> onNext()
                is LanguageIntent.SetLanguage -> vm.setLanguage(
                    language = intent.language,
                    context = context
                )
            }
        }
    )
}