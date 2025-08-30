package uz.yalla.client.feature.setting.model

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.model.type.ThemeType
import uz.yalla.client.feature.setting.components.Language
import uz.yalla.client.feature.setting.components.Theme
import uz.yalla.client.feature.settings.R

internal fun SettingsViewModel.observeLocaleChanges() = intent {
    viewModelScope.launch {
        prefs.locale.collectLatest { locale ->
            intent {
                reduce {
                    state.copy(
                        selectedLanguage = when (locale) {
                            "uz" -> Language(R.string.uzbek, "uz")
                            else -> Language(R.string.russian, "ru")
                        }
                    )
                }
            }
        }
    }
}

internal fun SettingsViewModel.observeThemeChanges() = intent {
    viewModelScope.launch {
        prefs.themeType.collectLatest { themeType ->
            intent {
                reduce {
                    state.copy(
                        selectedTheme = Theme(
                            themeType = themeType,
                            stringResId = when (themeType) {
                                ThemeType.LIGHT -> R.string.light_mode
                                ThemeType.DARK -> R.string.dark_mode
                                ThemeType.SYSTEM -> R.string.system_mode
                            }
                        )
                    )
                }
            }
        }
    }
}