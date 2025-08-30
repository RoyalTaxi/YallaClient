package uz.yalla.client.feature.info.about_app.intent

import androidx.annotation.StringRes

sealed interface AboutAppIntent {
    data object NavigateBack : AboutAppIntent
    data class NavigateToWeb(@StringRes val title: Int, val url: String) : AboutAppIntent
}