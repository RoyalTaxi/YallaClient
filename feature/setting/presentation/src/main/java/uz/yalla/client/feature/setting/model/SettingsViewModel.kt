package uz.yalla.client.feature.setting.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.feature.setting.components.Language
import uz.yalla.client.feature.settings.R

internal class SettingsViewModel(
    private val prefs: uz.yalla.client.core.domain.local.AppPreferences
) : ViewModel() {
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
    }


    fun setSelectedLanguageType(languageType: Language) = viewModelScope.launch(Dispatchers.Main) {
        _uiState.update { it.copy(selectedLanguage = languageType) }
        updateAppLanguage(languageType)
    }

    private fun updateAppLanguage(languageType: Language) {
        prefs.setLocale(languageType.languageTag)
    }
}