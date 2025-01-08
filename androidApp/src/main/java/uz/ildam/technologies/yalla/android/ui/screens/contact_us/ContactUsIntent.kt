package uz.ildam.technologies.yalla.android.ui.screens.contact_us

sealed interface ContactUsIntent {
    data object OnNavigateBack : ContactUsIntent
    data class OnClickUrl(val title: Int, val url: String) : ContactUsIntent
}