package uz.yalla.client.feature.android.setting.settings.view

import android.app.Activity
import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.feature.android.setting.settings.components.ChangeLanguageBottomSheet
import uz.yalla.client.feature.android.setting.settings.model.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsRoute(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    var changeLanguageVisibility by rememberSaveable { mutableStateOf(false) }
    val changeLanguageSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current as Activity

    SettingsScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                SettingsIntent.OnNavigateBack -> onNavigateBack()
                SettingsIntent.OnClickLanguage -> {
                    changeLanguageVisibility = true
                    scope.launch { changeLanguageSheetState.show() }
                }
            }
        }
    )

    if (changeLanguageVisibility) {
        ChangeLanguageBottomSheet(
            languages = uiState.languages,
            sheetState = changeLanguageSheetState,
            currentLanguage = uiState.selectedLanguage,
            onLanguageSelected = { language ->
                viewModel.setSelectedLanguageType(language)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.getSystemService(LocaleManager::class.java)
                        ?.applicationLocales = LocaleList.forLanguageTags(language.languageTag)
                } else {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(language.languageTag)
                    )
                }

                viewModel.notifyLanguageChange(language)
            },
            onDismissRequest = {
                changeLanguageVisibility = false
                scope.launch { changeLanguageSheetState.hide() }
            }
        )
    }
}