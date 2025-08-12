package uz.yalla.client.feature.auth.login.intent

data class LoginState(
    val phoneNumber: String,
    val sendButtonState: Boolean
) {
    companion object {
        val INITIAL = LoginState(
            phoneNumber = "",
            sendButtonState = false
        )
    }

    fun isPhoneNumberValid() = phoneNumber.length == 9
}