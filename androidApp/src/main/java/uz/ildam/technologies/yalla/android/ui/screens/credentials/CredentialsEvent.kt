package uz.ildam.technologies.yalla.android.ui.screens.credentials

sealed interface CredentialsEvent {
    data object Loading : CredentialsEvent
    data class Error(val error: String) : CredentialsEvent
    data class Success(val data: Unit) : CredentialsEvent
}