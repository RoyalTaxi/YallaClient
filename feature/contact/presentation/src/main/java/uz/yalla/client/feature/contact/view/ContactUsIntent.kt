package uz.yalla.client.feature.contact.view

internal sealed interface ContactUsIntent {
    data object OnNavigateBack : ContactUsIntent
    data class OnClickUrl(val title: Int, val url: String) : ContactUsIntent
    data class OnClickEmail(val email: String) : ContactUsIntent
    data class OnClickPhone(val phone: String) : ContactUsIntent
}