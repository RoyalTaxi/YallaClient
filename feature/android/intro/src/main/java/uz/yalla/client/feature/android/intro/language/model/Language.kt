package uz.yalla.client.feature.android.intro.language.model

import androidx.annotation.StringRes

internal data class Language(
    @StringRes val stringResId: Int,
    val languageTag: String,
)