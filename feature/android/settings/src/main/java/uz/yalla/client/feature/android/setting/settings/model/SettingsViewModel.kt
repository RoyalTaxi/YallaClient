package uz.yalla.client.feature.android.setting.settings.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.yalla.client.feature.android.setting.settings.components.Language

internal class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState = _uiState.asStateFlow()

    fun setSelectedLanguageType(languageType: Language) = viewModelScope.launch {
        _uiState.update { it.copy(selectedLanguage = languageType) }
        updateAppLanguage(languageType)
    }

    private fun updateAppLanguage(languageType: Language) {
        AppPreferences.locale = languageType.languageTag
    }

    fun notifyLanguageChange(language: Language) {
        _uiState.update { it.copy(selectedLanguage = language) }
    }
}