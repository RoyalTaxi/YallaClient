package uz.ildam.technologies.yalla.android.ui.screens.settings

import android.annotation.SuppressLint
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.activity.MainActivity
import uz.ildam.technologies.yalla.android.ui.sheets.ChangeLanguageBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    var changeLanguageVisibility by remember { mutableStateOf(false) }
    val changeLanguageSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current as MainActivity


    SettingsScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                SettingsIntent.OnNavigateBack -> {
                    onNavigateBack()
                }

                SettingsIntent.OnClickLanguage -> {
                    scope.launch {
                        changeLanguageVisibility = true
                        changeLanguageSheetState.show()
                    }
                }
            }
        }
    )

    if (changeLanguageVisibility) ChangeLanguageBottomSheet(
        languages = uiState.languages,
        sheetState = changeLanguageSheetState,
        currentLanguage = uiState.selectedLanguage,
        onLanguageSelected = { language ->
            viewModel.setSelectedLanguageType(language)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                context.getSystemService(LocaleManager::class.java)
                    .applicationLocales =
                    LocaleList.forLanguageTags(language.languageTag)
            else
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(language.languageTag)
                )
            context.recreate()
        },
        onDismissRequest = {
            scope.launch {
                changeLanguageVisibility = false
                changeLanguageSheetState.hide()
            }
        }
    )
}