package uz.yalla.client.feature.intro.language.model

import uz.yalla.client.feature.intro.R

internal data class LanguageUIState(
    val selectedLanguage: uz.yalla.client.feature.intro.language.model.Language? = null,
    val languages: List<uz.yalla.client.feature.intro.language.model.Language> = listOf(
        uz.yalla.client.feature.intro.language.model.Language(
            stringResId = R.string.uzbek,
            languageTag = uz.yalla.client.feature.intro.language.model.LanguageType.UZBEK.languageTag
        ),
        uz.yalla.client.feature.intro.language.model.Language(
            stringResId = R.string.russian,
            languageTag = uz.yalla.client.feature.intro.language.model.LanguageType.RUSSIAN.languageTag
        )
    )
)