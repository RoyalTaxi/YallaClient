package uz.ildam.technologies.yalla.core.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object AppPreferences {
    private const val GAME_PORTAL = "game_portal"
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(GAME_PORTAL, MODE_PRIVATE)
    }

    var locale: String
        get() = preferences.getString(AppPreferences::locale.name, "") ?: "uz"
        set(value) {
            preferences.edit()?.putString(AppPreferences::locale.name, value)?.apply()
        }

    var isDeviceRegistered: Boolean
        get() = preferences.getBoolean(AppPreferences::isDeviceRegistered.name, false)
        set(value) {
            preferences.edit().putBoolean(AppPreferences::isDeviceRegistered.name, value).apply()
        }

    var firstName: String
        get() = preferences.getString(AppPreferences::firstName.name, "") ?: "User"
        set(value) {
            preferences.edit()?.putString(AppPreferences::firstName.name, value)?.apply()
        }

    var lastName: String
        get() = preferences.getString(AppPreferences::lastName.name, "") ?: ""
        set(value) {
            preferences.edit()?.putString(AppPreferences::lastName.name, value)?.apply()
        }

    var accessToken: String
        get() = preferences.getString(AppPreferences::accessToken.name, "") ?: ""
        set(value) {
            preferences.edit()?.putString(AppPreferences::accessToken.name, value)?.apply()
        }

    var number: String
        get() = preferences.getString(AppPreferences::number.name, "") ?: ""
        set(value) {
            preferences.edit()?.putString(AppPreferences::number.name, value)?.apply()
        }

    var gender: String
        get() = preferences.getString(AppPreferences::gender.name, "") ?: ""
        set(value) {
            preferences.edit()?.putString(AppPreferences::gender.name, value)?.apply()
        }

    // format -> DD.MM.YYYY
    var dateOfBirth: String
        get() = preferences.getString(AppPreferences::dateOfBirth.name, "") ?: ""
        set(value) {
            preferences.edit()?.putString(AppPreferences::dateOfBirth.name, value)?.apply()
        }


}