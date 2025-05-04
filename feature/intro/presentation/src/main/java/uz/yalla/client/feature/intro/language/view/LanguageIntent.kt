package uz.yalla.client.feature.intro.language.view

import uz.yalla.client.feature.intro.language.model.Language

internal sealed interface LanguageIntent {
    data class SetLanguage(val language: Language) : LanguageIntent
    data object NavigateNext : LanguageIntent
}