package uz.yalla.client.feature.info.about_app.view

 sealed interface AboutAppIntent {
    data object OnNavigateBack : AboutAppIntent
    data class OnClickUrl(val title: Int, val url: String) : AboutAppIntent
}