package uz.yalla.client.feature.intro.language.model

import androidx.annotation.StringRes

data class Language(
    @StringRes val stringResId: Int,
    val languageTag: String,
)