package uz.yalla.client.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import uz.yalla.client.connectivity.ConnectivityObserver
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.feature.setting.domain.usecase.GetConfigUseCase
import uz.yalla.client.feature.setting.domain.usecase.SendFCMTokenUseCase

class MainViewModel(
    connectivityObserver: ConnectivityObserver,
    private val getConfigUseCase: GetConfigUseCase,
    private val sendFCMTokenUseCase: SendFCMTokenUseCase
) : ViewModel() {
    companion object {
        private const val MAX_LOCATION_ATTEMPTS = 3
        private const val DEFAULT_LOCATION_TIMEOUT = 5000L
    }

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            true
        )

    private val _isReady = MutableStateFlow<Boolean?>(null)
    val isReady = _isReady.asStateFlow()


    init {
        getConfig()
    }

    private fun getConfig() = viewModelScope.launch(Dispatchers.IO) {
        getConfigUseCase().onSuccess {
            AppPreferences.referralLink = it.setting.inviteLinkForFriend
            AppPreferences.becomeDrive = it.setting.executorLink
            AppPreferences.inviteFriends = it.setting.inviteLinkForFriend
        }
    }

    fun getLocationAndSave(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        var locationRetrieved = false
        var attempts = 0

        while (attempts < MAX_LOCATION_ATTEMPTS) {
            attempts++

            val result = withTimeoutOrNull(DEFAULT_LOCATION_TIMEOUT) {
                try {
                    var locationResult = false
                    getCurrentLocation(
                        context = context,
                        onLocationFetched = { location ->
                            AppPreferences.entryLocation =
                                Pair(location.latitude, location.longitude)
                            locationResult = true
                        },
                        onPermissionDenied = {
                            viewModelScope.launch(Dispatchers.Main.immediate) {
                                _isReady.emit(false)
                            }
                        }
                    )
                    locationResult
                } catch (e: Exception) {
                    false
                }
            }

            locationRetrieved = result == true

            if (locationRetrieved) {
                withContext(Dispatchers.Main) {
                    _isReady.emit(true)
                }
                break
            }
        }

        if (!locationRetrieved) {
            withContext(Dispatchers.Main) {
                _isReady.emit(false)
            }
        }
    }

    fun initializeFcm() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    AppPreferences.firebaseToken = task.result
                    if (AppPreferences.accessToken.isNotBlank()) sendFCMToken(task.result)
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