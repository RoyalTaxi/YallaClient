package uz.yalla.client.feature.intro.language.intent

import uz.yalla.client.feature.intro.language.model.Language

sealed interface LanguageSideEffect {
    data object NavigateOnboarding: LanguageSideEffect
    data class SetLanguage(val language: Language): LanguageSideEffect
}