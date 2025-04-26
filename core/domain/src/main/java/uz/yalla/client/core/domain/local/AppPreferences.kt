package uz.yalla.client.core.domain.local

import kotlinx.coroutines.flow.Flow
import uz.yalla.client.core.domain.model.MapType
import uz.yalla.client.core.domain.model.PaymentType

interface AppPreferences {
    val locale: Flow<String>
    fun setLocale(value: String)

    val tokenType: Flow<String>
    fun setTokenType(value: String)

    val accessToken: Flow<String>
    fun setAccessToken(value: String)

    val hasProcessedOrderOnEntry: Flow<Boolean>
    fun setHasProcessedOrderOnEntry(value: Boolean)

    val firebaseToken: Flow<String>
    fun setFirebaseToken(value: String)

    val isDeviceRegistered: Flow<Boolean>
    fun setDeviceRegistered(value: Boolean)

    val firstName: Flow<String>
    fun setFirstName(value: String)

    val lastName: Flow<String>
    fun setLastName(value: String)

    val number: Flow<String>
    fun setNumber(value: String)

    val gender: Flow<String>
    fun setGender(value: String)

    val dateOfBirth: Flow<String>
    fun setDateOfBirth(value: String)

    val mapType: Flow<MapType>
    fun setMapType(value: MapType)

    val paymentType: Flow<PaymentType>
    fun setPaymentType(value: PaymentType)

    val supportNumber: Flow<String>
    fun setSupportNumber(value: String)

    val referralLink: Flow<String>
    fun setReferralLink(value: String)

    val becomeDrive: Flow<String>
    fun setBecomeDrive(value: String)

    val inviteFriends: Flow<String>
    fun setInviteFriends(value: String)

    val entryLocation: Flow<Pair<Double, Double>>
    fun setEntryLocation(lat: Double, lng: Double)

    val maxBonus: Flow<Int>
    fun setMaxBonus(value: Int)

    val minBonus: Flow<Int>
    fun setMinBonus(value: Int)

    val balance: Flow<Int>
    fun setBalance(value: Int)

    val isBonusEnabled: Flow<Boolean>
    fun setBonusEnabled(value: Boolean)

    fun clearAll()
}