package uz.ildam.technologies.yalla.android.ui.screens.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LanguageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LanguageUIState())
    val uiState = _uiState.asStateFlow()

    fun setSelectedLanguageType(languageType: LanguageType?) = viewModelScope.launch {
        _uiState.update { it.copy(selectedLanguage = languageType) }
    }
}