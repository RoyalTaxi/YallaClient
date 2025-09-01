package uz.yalla.client.feature.info.about_app.intent

import androidx.annotation.StringRes

sealed interface AboutAppSideEffect {
    data object NavigateBack : AboutAppSideEffect
    data class NavigateWeb(@StringRes val title: Int, val url: String) : AboutAppSideEffect
}