package uz.yalla.client.feature.android.intro.language.view

import uz.yalla.client.feature.android.intro.language.model.Language

internal sealed interface LanguageIntent {
    data class SetLanguage(val language: Language) : LanguageIntent
    data object NavigateBack : LanguageIntent
    data object NavigateNext : LanguageIntent
}