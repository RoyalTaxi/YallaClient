package uz.yalla.client.feature.setting.settings.components

import androidx.annotation.StringRes

data class Language(
    @StringRes val stringResId: Int,
    val languageTag: String,
)