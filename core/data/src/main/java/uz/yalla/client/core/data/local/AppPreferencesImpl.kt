package uz.yalla.client.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.Preferences.Key
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapType
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.domain.model.type.ThemeType


internal class AppPreferencesImpl(
    private val store: DataStore<Preferences>
) : AppPreferences {
    private val scope = CoroutineScope(Dispatchers.IO)

    private object Prefs {
        val LOCALE = stringPreferencesKey("locale")
        val TOKEN_TYPE = stringPreferencesKey("tokenType")
        val ACCESS_TOKEN = stringPreferencesKey("accessToken")

        val HAS_PROCESSED = booleanPreferencesKey("hasProcessedOrderOnEntry")
        val FIREBASE_TOKEN = stringPreferencesKey("firebaseToken")
        val DEVICE_REGISTERED = booleanPreferencesKey("isDeviceRegistered")

        val FIRST_NAME = stringPreferencesKey("firstName")
        val LAST_NAME = stringPreferencesKey("lastName")
        val NUMBER = stringPreferencesKey("number")
        val GENDER = stringPreferencesKey("gender")
        val DOB = stringPreferencesKey("dateOfBirth")

        val MAP_TYPE = stringPreferencesKey("mapType")

        val PAYMENT_TYPE = stringPreferencesKey("paymentType")
        val CARD_ID = stringPreferencesKey("cardId")
        val CARD_NUMBER = stringPreferencesKey("cardNumber")

        val SUPPORT_NUMBER = stringPreferencesKey("supportNumber")
        val REFERRAL_LINK = stringPreferencesKey("referralLink")
        val BECOME_DRIVE = stringPreferencesKey("becomeDrive")
        val INVITE_FRIENDS = stringPreferencesKey("inviteFriends")

        val ENTRY_LOCATION = stringPreferencesKey("entryLocation")
        val MAX_BONUS = intPreferencesKey("maxBonus")
        val MIN_BONUS = intPreferencesKey("minBonus")
        val BALANCE = intPreferencesKey("balance")
        val IS_BONUS_ENABLED = booleanPreferencesKey("isBonusEnabled")
        val IS_CARD_ENABLED = booleanPreferencesKey("isCardEnabled")
        val SKIP_ONBOARDING = booleanPreferencesKey("skipOnboarding")
        val IS_VERIFICATION_REQUIRED = booleanPreferencesKey("isVerificationRequired")
        val THEME_TYPE = stringPreferencesKey("themeType")
    }

    private fun <T> get(key: Key<T>, default: T): Flow<T> =
        store.data.map { prefs -> prefs[key] ?: default }

    private suspend fun <T> set(key: Key<T>, value: T) =
        store.edit { prefs -> prefs[key] = value }

    override val locale = get(Prefs.LOCALE, "uz")
    override fun setLocale(value: String) {
        scope.launch { set(Prefs.LOCALE, value) }
    }

    override val accessToken = get(Prefs.ACCESS_TOKEN, "")
    override fun setAccessToken(value: String) {
        scope.launch { set(Prefs.ACCESS_TOKEN, value) }
    }

    override val hasProcessedOrderOnEntry = get(Prefs.HAS_PROCESSED, false)
    override fun setHasProcessedOrderOnEntry(value: Boolean) {
        scope.launch { set(Prefs.HAS_PROCESSED, value) }
    }

    override val firebaseToken = get(Prefs.FIREBASE_TOKEN, "")
    override fun setFirebaseToken(value: String) {
        scope.launch { set(Prefs.FIREBASE_TOKEN, value) }
    }

    override val isDeviceRegistered = get(Prefs.DEVICE_REGISTERED, false)
    override fun setDeviceRegistered(value: Boolean) {
        scope.launch { set(Prefs.DEVICE_REGISTERED, value) }
    }

    override val firstName = get(Prefs.FIRST_NAME, "User")
    override fun setFirstName(value: String) {
        scope.launch { set(Prefs.FIRST_NAME, value) }
    }

    override val lastName = get(Prefs.LAST_NAME, "")
    override fun setLastName(value: String) {
        scope.launch { set(Prefs.LAST_NAME, value) }
    }

    override val number = get(Prefs.NUMBER, "")
    override fun setNumber(value: String) {
        scope.launch { set(Prefs.NUMBER, value) }
    }

    override val gender = get(Prefs.GENDER, "")
    override fun setGender(value: String) {
        scope.launch { set(Prefs.GENDER, value) }
    }

    override val dateOfBirth = get(Prefs.DOB, "")
    override fun setDateOfBirth(value: String) {
        scope.launch { set(Prefs.DOB, value) }
    }

    override val mapType: Flow<MapType> = store.data.map { prefs ->
        val name = prefs[Prefs.MAP_TYPE] ?: MapType.Google.typeName
        MapType.fromTypeName(name)
    }

    override fun setMapType(value: MapType) {
        scope.launch { set(Prefs.MAP_TYPE, value.typeName) }
    }

    override val paymentType: Flow<PaymentType> = store.data.map { prefs ->
        val typeName = prefs[Prefs.PAYMENT_TYPE] ?: PaymentType.CASH.typeName
        val cardId = prefs[Prefs.CARD_ID] ?: ""
        val cardNumber = prefs[Prefs.CARD_NUMBER] ?: ""
        PaymentType.fromTypeName(typeName, cardId, cardNumber)
    }

    override fun setPaymentType(value: PaymentType) {
        scope.launch {
            store.edit { prefs ->
                prefs[Prefs.PAYMENT_TYPE] = value.typeName
                if (value is PaymentType.CARD) {
                    prefs[Prefs.CARD_ID] = value.cardId
                    prefs[Prefs.CARD_NUMBER] = value.cardNumber
                } else {
                    prefs -= Prefs.CARD_ID
                    prefs -= Prefs.CARD_NUMBER
                }
            }
        }
    }

    override val supportNumber = get(Prefs.SUPPORT_NUMBER, "")
    override fun setSupportNumber(value: String) {
        scope.launch { set(Prefs.SUPPORT_NUMBER, value) }
    }

    override val referralLink = get(Prefs.REFERRAL_LINK, "")
    override fun setReferralLink(value: String) {
        scope.launch { set(Prefs.REFERRAL_LINK, value) }
    }

    override val becomeDrive = get(Prefs.BECOME_DRIVE, "")
    override fun setBecomeDrive(value: String) {
        scope.launch { set(Prefs.BECOME_DRIVE, value) }
    }

    override val inviteFriends = get(Prefs.INVITE_FRIENDS, "")
    override fun setInviteFriends(value: String) {
        scope.launch { set(Prefs.INVITE_FRIENDS, value) }
    }

    override val entryLocation: Flow<Pair<Double, Double>> =
        store.data.map { prefs ->
            val raw = prefs[Prefs.ENTRY_LOCATION] ?: ""
            raw
                .split(",", limit = 2)
                .let { parts ->
                    val lat = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
                    val lng = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
                    lat to lng
                }
        }

    override fun setEntryLocation(lat: Double, lng: Double) {
        scope.launch {
            set(Prefs.ENTRY_LOCATION, "$lat,$lng")
        }
    }

    override val maxBonus: Flow<Int> = get(Prefs.MAX_BONUS, 0)
    override fun setMaxBonus(value: Int) {
        scope.launch { set(Prefs.MAX_BONUS, value) }
    }

    override val minBonus: Flow<Int> = get(Prefs.MIN_BONUS, 0)
    override fun setMinBonus(value: Int) {
        scope.launch { set(Prefs.MIN_BONUS, value) }
    }

    override val balance: Flow<Int> = get(Prefs.BALANCE, 0)
    override fun setBalance(value: Int) {
        scope.launch { set(Prefs.BALANCE, value) }
    }

    override val isBonusEnabled: Flow<Boolean> = get(Prefs.IS_BONUS_ENABLED, false)
    override fun setBonusEnabled(value: Boolean) {
        scope.launch { set(Prefs.IS_BONUS_ENABLED, value) }
    }

    override val isCardEnabled: Flow<Boolean> = get(Prefs.IS_CARD_ENABLED, false)
    override fun setCardEnabled(value: Boolean) {
        scope.launch { set(Prefs.IS_CARD_ENABLED, value) }
    }

    override val skipOnboarding: Flow<Boolean> = get(Prefs.SKIP_ONBOARDING, false)
    override fun setSkipOnboarding(value: Boolean) {
        scope.launch { set(Prefs.SKIP_ONBOARDING, value) }
    }

    override val isVerificationRequired: Flow<Boolean> = get(Prefs.IS_VERIFICATION_REQUIRED, false)
    override fun setIsVerificationRequired(value: Boolean) {
        scope.launch { set(Prefs.IS_VERIFICATION_REQUIRED, value) }
    }

    override val themeType: Flow<ThemeType> = store.data.map { prefs ->
        val typeName = prefs[Prefs.THEME_TYPE] ?: ThemeType.SYSTEM.name
        ThemeType.fromValue(typeName)
    }
    override fun setThemeType(value: ThemeType) {
        scope.launch { set(Prefs.THEME_TYPE, value.name) }
    }

    override fun performLogout() {
        scope.launch {
            store.edit { prefs ->
                prefs.clear()
                prefs[Prefs.SKIP_ONBOARDING] = true
            }
        }
    }
}