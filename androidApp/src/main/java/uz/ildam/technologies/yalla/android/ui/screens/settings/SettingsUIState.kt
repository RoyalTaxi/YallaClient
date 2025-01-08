package uz.ildam.technologies.yalla.android.ui.screens.settings

import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.core.data.local.AppPreferences

data class SettingsUIState(
    val selectedLanguage: Language = when (AppPreferences.locale) {
        "uz" -> Language(R.string.uzbek, "uz")
        else -> Language(R.string.russian, "ru")
    },
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