package uz.yalla.client.feature.auth.login.intent

sealed interface LoginIntent {
    data class SendCode(val hash: String) : LoginIntent
    data class UpdatePhoneNumber(val number: String) : LoginIntent
}