package uz.yalla.client.feature.contact.contact_us.model

internal sealed interface ContactUsActionState {
    data object Loading: ContactUsActionState
    data object Error: ContactUsActionState
    data object Success: ContactUsActionState
}