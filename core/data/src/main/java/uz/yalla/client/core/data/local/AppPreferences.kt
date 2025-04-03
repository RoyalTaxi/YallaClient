package uz.yalla.client.core.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import uz.yalla.client.core.data.enums.MapType
import uz.yalla.client.core.data.enums.PaymentType

object AppPreferences {
    private const val YALLA = "yalla"
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(YALLA, MODE_PRIVATE)
    }

    fun clear() {
        preferences.edit()?.clear()?.apply()
    }

    var locale: String
        get() = preferences.getString(AppPreferences::locale.name, "uz") ?: "uz"
        set(value) {
            preferences.edit()?.putString(AppPreferences::locale.name, value)?.apply()
        }

    var firebaseToken: String
        get() = preferences.getString(AppPreferences::firebaseToken.name, "") ?: ""
        set(value) {
            preferences.edit()?.putString(AppPreferences::firebaseToken.name, value)?.apply()
        }

    var lastOrderId: Int
        get() = preferences.getInt(AppPreferences::lastOrderId.name, -1)
        set(value) {
            preferences.edit()?.putInt(AppPreferences::lastOrderId.name, value)?.apply()
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

    var tokenType: String
        get() = preferences.getString(AppPreferences::tokenType.name, "") ?: ""
        set(value) {
            preferences.edit()?.putString(AppPreferences::tokenType.name, value)?.apply()
        }

    var mapType: MapType
        get() {
            val typeName =
                preferences.getString(AppPreferences::mapType.name, MapType.Google.typeName)
            return MapType.fromTypeName(typeName ?: MapType.Google.typeName)
        }
        set(value) {
            preferences.edit()?.putString(AppPreferences::mapType.name, value.typeName)?.apply()
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

    private var cardId: String
        get() = preferences.getString(AppPreferences::cardId.name, "") ?: ""
        set(value) {
            preferences.edit()?.putString(AppPreferences::cardId.name, value)?.apply()
        }

    private var cardNumber: String
        get() = preferences.getString(AppPreferences::cardNumber.name, "") ?: ""
        set(value) {
            preferences.edit()?.putString(AppPreferences::cardNumber.name, value)?.apply()
        }

    var paymentType: PaymentType
        get() {
            val typeName =
                preferences.getString(AppPreferences::paymentType.name, PaymentType.CASH.typeName)
                    ?: PaymentType.CASH.typeName
            val cardId = preferences.getString(AppPreferences::cardId.name, "")
            val cardNumber = preferences.getString(AppPreferences::cardNumber.name, "")
            return PaymentType.fromTypeName(typeName, cardId, cardNumber)
        }
        set(value) {
            preferences.edit().apply {
                putString(AppPreferences::paymentType.name, value.typeName)
                if (value is PaymentType.CARD) {
                    putString(AppPreferences::cardId.name, value.cardId)
                    putString(AppPreferences::cardNumber.name, value.cardNumber)
                } else {
                    remove(AppPreferences::cardId.name)
                    remove(AppPreferences::cardNumber.name)
                }
            }.apply()
        }

    var supportNumber: String
        get() = preferences.getString(AppPreferences::supportNumber.name, "") ?: ""
        set(value) {
            preferences.edit()?.putString(AppPreferences::supportNumber.name, value)?.apply()
        }

    var referralLink: String
        get() = preferences.getString(AppPreferences::referralLink.name, "") ?: ""
        set(value) {
            preferences.edit()?.putString(AppPreferences::referralLink.name, value)?.apply()
        }

    var becomeDrive: String
        get() = preferences.getString(AppPreferences::becomeDrive.name, "") ?: ""
        set(value) {
            preferences.edit()?.putString(AppPreferences::becomeDrive.name, value)?.apply()
        }

    var inviteFriends: String
        get() = preferences.getString(AppPreferences::inviteFriends.name, "") ?: ""
        set(value) {
            preferences.edit()?.putString(AppPreferences::inviteFriends.name, value)?.apply()
        }

    var entryLocation: Pair<Double, Double>
        get() {
            val stringValue = preferences.getString(AppPreferences::entryLocation.name, "") ?: ""
            if (stringValue.isNotEmpty()) {
                val parts = stringValue.split(",", limit = 2)
                val lat = parts.getOrNull(0)?.toDoubleOrNull()
                val lng = parts.getOrNull(1)?.toDoubleOrNull()

                if (lat != null && lng != null) {
                    return lat to lng
                }
            }
            return 0.0 to 0.0
        }
        set(value) {
            val stringValue = "${value.first},${value.second}"
            preferences.edit()
                .putString(AppPreferences::entryLocation.name, stringValue)
                .apply()
        }

}
