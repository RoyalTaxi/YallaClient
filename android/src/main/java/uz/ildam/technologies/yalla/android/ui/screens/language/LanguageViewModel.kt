package uz.ildam.technologies.yalla.android.ui.screens.language

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uz.ildam.technologies.yalla.core.data.local.AppPreferences

class LanguageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LanguageUIState())
    val uiState = _uiState.asStateFlow()

    fun setLanguage(language: Language, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            context.getSystemService(LocaleManager::class.java)
                ?.applicationLocales =
                LocaleList.forLanguageTags(language.languageTag)
        else
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(language.languageTag)
            )

        _uiState.update { it.copy(selectedLanguage = language) }
        AppPreferences.locale = language.languageTag
    }
}