package uz.yalla.client.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import uz.yalla.client.connectivity.ConnectivityObserver
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.feature.setting.domain.usecase.GetConfigUseCase
import uz.yalla.client.feature.setting.domain.usecase.SendFCMTokenUseCase
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

class MainViewModel(
    connectivityObserver: ConnectivityObserver,
    private val getConfigUseCase: GetConfigUseCase,
    private val sendFCMTokenUseCase: SendFCMTokenUseCase,
    private val prefs: AppPreferences
) : ViewModel() {
    companion object {
        private const val MAX_LOCATION_ATTEMPTS = 3
        private val DEFAULT_LOCATION_TIMEOUT = 5.seconds
    }

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    private val _isReady = MutableStateFlow<Boolean?>(null)
    val isReady = _isReady.asStateFlow()

    private val accessToken = MutableStateFlow("")

    init {
        getConfig()
        viewModelScope.launch {
            prefs.accessToken.collectLatest { accessToken.value = it }
        }
    }

    private fun getConfig() = viewModelScope.launch(Dispatchers.IO) {
        getConfigUseCase().onSuccess { result ->
            prefs.setReferralLink(result.setting.inviteLinkForFriend)
            prefs.setBecomeDrive(result.setting.executorLink)
            prefs.setInviteFriends(result.setting.inviteLinkForFriend)
        }
    }

    fun getLocationAndSave(context: Context) = viewModelScope.launch(Dispatchers.IO) {

        repeat(MAX_LOCATION_ATTEMPTS) {
            val coords = withTimeoutOrNull(DEFAULT_LOCATION_TIMEOUT) {
                fetchLocationOnce(context)
            }

            if (coords != null) {
                prefs.setEntryLocation(lat = coords.first, lng = coords.second)
                withContext(Dispatchers.Main) { _isReady.emit(true) }
                return@launch
            }
        }

        withContext(Dispatchers.Main) { _isReady.emit(false) }
    }

    private suspend fun fetchLocationOnce(context: Context): Pair<Double, Double>? =
        suspendCancellableCoroutine { cont ->
            getCurrentLocation(
                context = context,
                onLocationFetched = { location ->
                    cont.resume(location.latitude to location.longitude)
                },
                onPermissionDenied = {
                    cont.resume(null)
                }
            )
        }

    fun initializeFcm() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    prefs.setFirebaseToken(token)
                    if (accessToken.value.isNotBlank()) {
                        sendFCMToken(token)
                    }
                }
            }
        }
    }

    private fun sendFCMToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sendFCMTokenUseCase(token)
        }
    }
}