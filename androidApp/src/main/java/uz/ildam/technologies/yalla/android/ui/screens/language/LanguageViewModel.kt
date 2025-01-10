package uz.ildam.technologies.yalla.android.ui.screens.language

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LanguageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LanguageUIState())
    val uiState = _uiState.asStateFlow()

    fun notifyLanguageChange(language: Language) {
        _uiState.update { it.copy(selectedLanguage = language) }
    }
}