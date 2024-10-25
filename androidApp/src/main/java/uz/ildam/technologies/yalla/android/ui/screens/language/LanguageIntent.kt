package uz.ildam.technologies.yalla.android.ui.screens.language

sealed interface LanguageIntent {
    data class SetLanguage(val language: Language) : LanguageIntent
    data object NavigateBack : LanguageIntent
    data object NavigateNext : LanguageIntent
}