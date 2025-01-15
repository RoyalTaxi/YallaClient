package uz.yalla.client.feature.android.settings.settings.components

import androidx.annotation.StringRes

data class Language(
    @StringRes val stringResId: Int,
    val languageTag: String,
)