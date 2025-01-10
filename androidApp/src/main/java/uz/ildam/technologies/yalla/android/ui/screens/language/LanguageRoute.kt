package uz.ildam.technologies.yalla.android.ui.screens.language

import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.activity.MainActivity
import uz.ildam.technologies.yalla.core.data.local.AppPreferences

@Composable
internal fun LanguageRoute(
    onBack: () -> Unit,
    onNext: () -> Unit,
    vm: LanguageViewModel = koinViewModel()
) {
    val context = LocalContext.current as MainActivity
    val uiState by vm.uiState.collectAsState()

    LanguageScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is LanguageIntent.NavigateBack -> onBack()
                is LanguageIntent.NavigateNext -> {
                    uiState.selectedLanguage?.languageTag?.let { AppPreferences.locale = it }
                    onNext()
                }

                is LanguageIntent.SetLanguage -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.getSystemService(LocaleManager::class.java)
                            ?.applicationLocales =
                            LocaleList.forLanguageTags(intent.language.languageTag)
                    } else {
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(intent.language.languageTag)
                        )
                    }
                    vm.notifyLanguageChange(intent.language)
                }
            }
        }
    )
}