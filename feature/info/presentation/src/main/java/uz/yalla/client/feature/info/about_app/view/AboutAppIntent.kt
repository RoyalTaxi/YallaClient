package uz.yalla.client.feature.info.about_app.view

internal sealed interface AboutAppIntent {
    data object OnNavigateBack : AboutAppIntent
    data class OnClickUrl(val title: Int, val url: String) : AboutAppIntent
}