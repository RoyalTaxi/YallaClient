package uz.ildam.technologies.yalla.android.ui.screens.login

sealed interface LoginIntent {
    data object NavigateBack : LoginIntent
    data object ClearFocus : LoginIntent
    data object SendCode : LoginIntent
    data class SetNumber(val number: String) : LoginIntent
}