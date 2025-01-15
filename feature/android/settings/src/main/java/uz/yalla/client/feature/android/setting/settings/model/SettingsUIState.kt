package uz.yalla.client.feature.android.setting.settings.model

import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.yalla.client.feature.android.setting.settings.components.Language
import uz.yalla.client.feature.android.setting.settings.components.LanguageType
import uz.yalla.client.feature.android.settings.R

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