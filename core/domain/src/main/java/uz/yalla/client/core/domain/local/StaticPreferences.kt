package uz.yalla.client.core.domain.local

interface StaticPreferences {
    var accessToken: String
    var skipOnboarding: Boolean
    var isDeviceRegistered: Boolean
    fun performLogout()
}
