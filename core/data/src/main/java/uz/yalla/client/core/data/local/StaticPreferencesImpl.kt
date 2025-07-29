package uz.yalla.client.core.data.local

import android.content.Context
import androidx.core.content.edit
import uz.yalla.client.core.domain.local.StaticPreferences

class StaticPreferencesImpl(val context: Context) : StaticPreferences {

    companion object {
        private const val PREFERENCES = "preferences"
    }

    private var preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    override var accessToken: String
        get() = preferences.getString(::accessToken.name, "").orEmpty()
        set(value) = preferences.edit { putString(::accessToken.name, value) }

    override var skipOnboarding: Boolean
        get() = preferences.getBoolean(::skipOnboarding.name, false)
        set(value) = preferences.edit { putBoolean(::skipOnboarding.name, value) }

    override var isDeviceRegistered: Boolean
        get() = preferences.getBoolean(::isDeviceRegistered.name, false)
        set(value) = preferences.edit { putBoolean(::isDeviceRegistered.name, value) }

    override var hasProcessedOrderOnEntry: Boolean
        get() = preferences.getBoolean(::hasProcessedOrderOnEntry.name, false)
        set(value) = preferences.edit { putBoolean(::hasProcessedOrderOnEntry.name, value) }

}
