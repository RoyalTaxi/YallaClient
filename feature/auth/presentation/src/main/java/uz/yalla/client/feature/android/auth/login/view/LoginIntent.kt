package uz.yalla.client.feature.android.auth.login.view

internal sealed interface LoginIntent {
    data object NavigateBack : LoginIntent
    data object ClearFocus : LoginIntent
    data object SendCode : LoginIntent
    data class SetNumber(val number: String) : LoginIntent
}