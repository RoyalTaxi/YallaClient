package uz.yalla.client.feature.setting.view

import android.app.Activity
import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.type.ThemeType
import uz.yalla.client.feature.setting.components.ChangeLanguageBottomSheet
import uz.yalla.client.feature.setting.components.ChangeThemeBottomSheet
import uz.yalla.client.feature.setting.model.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsRoute(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val changeLanguageSheetState = rememberModalBottomSheetState()
    val changeThemeSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current as Activity

    SettingsScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                SettingsIntent.OnNavigateBack -> onNavigateBack()
                SettingsIntent.OnClickLanguage -> {
                    viewModel.setChangeLanguageVisibility(true)
                    scope.launch { changeLanguageSheetState.show() }
                }

                SettingsIntent.OnClickTheme -> {
                    viewModel.setChangeThemeVisibility(true)
                    scope.launch { changeLanguageSheetState.show()}
                }
            }
        }
    )

    if (uiState.changeLanguageSheetVisibility) {
        ChangeLanguageBottomSheet(
            languages = uiState.languages,
            sheetState = changeLanguageSheetState,
            currentLanguage = uiState.selectedLanguage,
            onLanguageSelected = { language ->
                viewModel.setSelectedLanguageType(language)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.getSystemService(LocaleManager::class.java)
                        .applicationLocales =
                        LocaleList.forLanguageTags(language.languageTag)
                } else {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(language.languageTag)
                    )
                }
            },
            onDismissRequest = {
                viewModel.setChangeLanguageVisibility(false)
                scope.launch { changeLanguageSheetState.hide() }
            }
        )
    }

    if (uiState.changeThemeSheetVisibility) {
        ChangeThemeBottomSheet(
            themes = uiState.themes,
            sheetState = changeThemeSheetState,
            currentTheme = uiState.selectedTheme,
            onThemeChange = { theme ->
                viewModel.setThemeType(theme)
            },
            onDismissRequest = {
                viewModel.setChangeThemeVisibility(false)
                scope.launch { changeThemeSheetState.hide() }
            }
        )
    }
}