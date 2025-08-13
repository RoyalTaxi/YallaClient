package uz.yalla.client.feature.auth.login.intent

sealed interface LoginSideEffect {
    data object NavigateBack : LoginSideEffect
    data class NavigateToVerification(val phoneNumber: String, val seconds: Int) : LoginSideEffect
}