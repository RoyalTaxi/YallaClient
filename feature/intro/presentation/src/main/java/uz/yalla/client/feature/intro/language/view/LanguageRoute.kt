package uz.yalla.client.feature.intro.language.view

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.feature.intro.language.model.LanguageViewModel

@Composable
internal fun LanguageRoute(
    onNext: () -> Unit,
    vm: LanguageViewModel = koinViewModel()
) {
    val context = LocalContext.current as Activity
    val uiState by vm.uiState.collectAsState()

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