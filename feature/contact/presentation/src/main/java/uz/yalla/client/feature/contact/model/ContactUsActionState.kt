package uz.yalla.client.feature.contact.model

internal sealed interface ContactUsActionState {
    data object Loading: ContactUsActionState
    data object Error: ContactUsActionState
    data object Success: ContactUsActionState
}