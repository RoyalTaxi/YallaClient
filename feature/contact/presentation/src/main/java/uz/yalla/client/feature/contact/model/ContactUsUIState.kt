package uz.yalla.client.feature.contact.model

internal data class ContactUsUIState (
    val socialNetworks: List<Triple<Int, String, Int>> = emptyList()
)