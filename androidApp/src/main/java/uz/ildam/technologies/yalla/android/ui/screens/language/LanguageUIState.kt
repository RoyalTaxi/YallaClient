package uz.ildam.technologies.yalla.android.ui.screens.language

import uz.ildam.technologies.yalla.android.R

data class LanguageUIState(
    val selectedLanguage: LanguageType? = null,
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