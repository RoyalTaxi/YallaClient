package uz.yalla.client.feature.intro.language.model

import uz.yalla.client.feature.intro.language.intent.LanguageIntent
import uz.yalla.client.feature.intro.language.intent.LanguageSideEffect

internal fun LanguageViewModel.onIntent(intent: LanguageIntent) = intent {
    when (intent) {
        LanguageIntent.NavigateNext -> {
            postSideEffect(LanguageSideEffect.NavigateOnboarding)
        }
        is LanguageIntent.SetLanguage -> {
            postSideEffect(LanguageSideEffect.SetLanguage(intent.language))
        }
    }
}