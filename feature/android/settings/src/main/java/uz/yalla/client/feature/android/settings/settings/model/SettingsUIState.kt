package uz.yalla.client.feature.android.settings.settings.model

import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.yalla.client.feature.android.settings.R
import uz.yalla.client.feature.android.settings.settings.components.Language
import uz.yalla.client.feature.android.settings.settings.components.LanguageType

internal data class SettingsUIState(
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