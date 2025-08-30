package uz.yalla.client.feature.setting.view

import android.app.Activity
import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.lifecycle.MakeBridge
import uz.yalla.client.feature.setting.components.ChangeLanguageBottomSheet
import uz.yalla.client.feature.setting.components.ChangeThemeBottomSheet
import uz.yalla.client.feature.setting.intent.SettingsSideEffect
import uz.yalla.client.feature.setting.model.SettingsViewModel
import uz.yalla.client.feature.setting.model.onIntent
import uz.yalla.client.feature.setting.model.setChangeLanguageVisibility
import uz.yalla.client.feature.setting.model.setChangeThemeVisibility
import uz.yalla.client.feature.setting.model.setSelectedLanguageType
import uz.yalla.client.feature.setting.model.setThemeType
import uz.yalla.client.feature.setting.navigation.FromSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsRoute(
    navigateTo: (FromSettings) -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val context = LocalActivity.current as Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    val changeLanguageSheetState = rememberModalBottomSheetState()
    val changeThemeSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val uiState by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    lifecycleOwner.MakeBridge(viewModel)

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SettingsSideEffect.ClickLanguage ->  scope.launch { changeLanguageSheetState.show() }
            SettingsSideEffect.ClickTheme -> scope.launch { changeThemeSheetState.show() }
            SettingsSideEffect.NavigateBack -> navigateTo(FromSettings.NavigateBack)
        }
    }

    SettingsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
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