package uz.yalla.client.feature.contact.contact_us.model

internal data class ContactUsUIState (
    val socialNetworks: List<Triple<Int, String, Int>> = emptyList()
)