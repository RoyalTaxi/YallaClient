package uz.yalla.client.feature.contact.intent

import androidx.annotation.StringRes

sealed interface ContactUsSideEffect {
    data object NavigateBack : ContactUsSideEffect
    data class NavigateWeb(@StringRes val title: Int, val url: String) : ContactUsSideEffect
    data class NavigateToEmail(val email: String) : ContactUsSideEffect
    data class NavigateToPhoneCall(val phoneNumber: String) : ContactUsSideEffect
}