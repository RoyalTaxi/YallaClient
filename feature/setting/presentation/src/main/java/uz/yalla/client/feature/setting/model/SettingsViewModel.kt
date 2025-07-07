package uz.yalla.client.feature.setting.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.type.ThemeType
import uz.yalla.client.feature.setting.components.Language
import uz.yalla.client.feature.setting.components.Theme
import uz.yalla.client.feature.settings.R

internal class SettingsViewModel(
    private val prefs: AppPreferences
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.locale.collectLatest { locale ->
                _uiState.update {
                    it.copy(
                        selectedLanguage = when (locale) {
                            "uz" -> Language(R.string.uzbek, "uz")
                            else -> Language(R.string.russian, "ru")
                        }
                    )
                }
            }
        }

        viewModelScope.launch {
            prefs.themeType.collectLatest { themType ->
                _uiState.update {
                    it.copy(
                        selectedTheme = Theme(
                            themeType = themType,
                            stringResId = when (themType) {
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


    fun setSelectedLanguageType(languageType: Language) = viewModelScope.launch(Dispatchers.Main) {
        _uiState.update { it.copy(selectedLanguage = languageType) }
        updateAppLanguage(languageType)
    }

    fun setThemeType(theme: Theme) = viewModelScope.launch(Dispatchers.Main) {
        _uiState.update { it.copy(selectedTheme = theme) }
        updateAppTheme(theme.themeType)
    }

    fun setChangeLanguageVisibility(value: Boolean) {
        _uiState.update { it.copy(changeLanguageSheetVisibility = value) }
    }

    fun setChangeThemeVisibility(value: Boolean) {
        _uiState.update { it.copy(changeThemeSheetVisibility = value) }
    }

    private fun updateAppLanguage(languageType: Language) {
        prefs.setLocale(languageType.languageTag)
    }

    private fun updateAppTheme(themeType: ThemeType) {
        prefs.setThemeType(themeType)
    }
}