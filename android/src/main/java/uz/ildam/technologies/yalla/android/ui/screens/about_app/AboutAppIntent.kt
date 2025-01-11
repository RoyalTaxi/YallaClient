package uz.ildam.technologies.yalla.android.ui.screens.about_app

sealed interface AboutAppIntent {
    data object OnNavigateBack : AboutAppIntent
    data class OnClickUrl(val title: Int, val url: String) : AboutAppIntent
}