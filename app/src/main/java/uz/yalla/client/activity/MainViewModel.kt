package uz.yalla.client.activity

import uz.yalla.client.core.common.viewmodel.BaseViewModel
import android.content.Context
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
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.feature.setting.domain.usecase.GetConfigUseCase
import uz.yalla.client.feature.setting.domain.usecase.RefreshFCMTokenUseCase
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

class MainViewModel(
    private val appContext: Context,
    connectivityObserver: ConnectivityObserver,
    private val getConfigUseCase: GetConfigUseCase,
    private val refreshFCMTokenUseCase: RefreshFCMTokenUseCase,
    private val prefs: AppPreferences
) : BaseViewModel() {

    companion object {
        private const val MAX_LOCATION_ATTEMPTS = 3
        private val DEFAULT_LOCATION_TIMEOUT = 2.seconds
    }

    val isConnected = connectivityObserver.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    private val _isReady = MutableStateFlow<ReadyState>(ReadyState.Loading)
    val isReady = _isReady.asStateFlow()

    val isDeviceRegistered = prefs.isDeviceRegistered
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val skipOnboarding = prefs.skipOnboarding
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            isDeviceRegistered.collectLatest { isRegistered ->
                when (isRegistered) {
                    true -> getLocationAndSave(appContext)
                    false -> _isReady.emit(ReadyState.Ready)
                    else -> Unit
                }
            }
        }

        getConfig()
    }

    private fun getConfig() = viewModelScope.launch {
        getConfigUseCase().onSuccess { result ->
            prefs.setReferralLink(result.setting.inviteLinkForFriend)
            prefs.setBecomeDrive(result.setting.executorLink)
            prefs.setInviteFriends(result.setting.inviteLinkForFriend)
            prefs.setCardEnabled(result.setting.isCardEnabled)

            if (result.setting.isCardEnabled)
                prefs.setPaymentType(PaymentType.CASH)
        }
    }

    private fun getLocationAndSave(context: Context) = viewModelScope.launch {
        repeat(MAX_LOCATION_ATTEMPTS) {
            val coordinates = withTimeoutOrNull(DEFAULT_LOCATION_TIMEOUT) {
                fetchLocationOnce(context)
            }

            if (coordinates != null) {
                prefs.setEntryLocation(lat = coordinates.first, lng = coordinates.second)
                withContext(Dispatchers.Main) {
                    _isReady.emit(ReadyState.Ready)
                }
                return@launch
            }
        }

        withContext(Dispatchers.Main) {
            _isReady.emit(ReadyState.Failed)
        }
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

    fun refreshFCMToken() {
        viewModelScope.launch { refreshFCMTokenUseCase() }
    }
}