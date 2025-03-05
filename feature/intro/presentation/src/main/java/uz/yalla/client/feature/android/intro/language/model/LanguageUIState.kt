package uz.yalla.client.feature.android.intro.language.model

import uz.yalla.client.feature.android.intro.R

internal data class LanguageUIState(
    val selectedLanguage: Language? = null,
    val languages: List<Language> = listOf(
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