package uz.yalla.client.feature.intro.language.intent

import uz.yalla.client.feature.intro.R
import uz.yalla.client.feature.intro.language.model.Language
import uz.yalla.client.feature.intro.language.model.LanguageType

internal data class LanguageState(
    val selectedLanguage: Language?,
    val languages: List<Language>
) {
    companion object {
        val INTERNAL = LanguageState(
            selectedLanguage = null,
            languages = listOf(
                Language(
                    stringResId = R.string.uzbek,
                    languageTag = LanguageType.UZBEK.languageTag
                ),
                Language(
                    stringResId = R.string.russian,
                    languageTag = LanguageType.RUSSIAN.languageTag
                )
            )
        )
    }
}