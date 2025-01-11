package uz.ildam.technologies.yalla.android.ui.screens.settings

import androidx.annotation.StringRes

data class Language(
    @StringRes val stringResId: Int,
    val languageTag: String,
)