package uz.yalla.client.feature.setting.model

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.model.type.ThemeType
import uz.yalla.client.feature.setting.components.Language
import uz.yalla.client.feature.setting.components.Theme

internal fun SettingsViewModel.setSelectedLanguageType(languageType: Language) =
    viewModelScope.launch( Dispatchers.Main) {
        intent {
            reduce {
                state.copy(selectedLanguage = languageType)
            }
        }
        updateAppLanguage(languageType)
    }

internal fun SettingsViewModel.setThemeType(theme: Theme) = viewModelScope.launch(Dispatchers.Main) {
    intent {
        reduce {
            state.copy(selectedTheme = theme)
        }
    }
    updateAppTheme(theme.themeType)
}

internal fun SettingsViewModel.setChangeLanguageVisibility(value: Boolean) = intent {
    reduce {
        state.copy(changeLanguageSheetVisibility = value)
    }
}

internal fun SettingsViewModel.setChangeThemeVisibility(value: Boolean) = intent {
    reduce {
        state.copy(changeThemeSheetVisibility = value)
    }
}

private fun SettingsViewModel.updateAppLanguage(languageType: Language) {
    prefs.setLocale(languageType.languageTag)
}

private fun SettingsViewModel.updateAppTheme(themeType: ThemeType) {
    prefs.setThemeType(themeType)
}