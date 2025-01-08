package uz.ildam.technologies.yalla.android.ui.screens.contact_us

sealed interface ContactUsActionState {
    data object Loading: ContactUsActionState
    data object Error: ContactUsActionState
    data object Success: ContactUsActionState
}