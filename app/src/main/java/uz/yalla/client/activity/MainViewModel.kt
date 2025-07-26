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
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.local.StaticPreferences
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
    private val appPreferences: AppPreferences,
    private val staticPreferences: StaticPreferences
) : BaseViewModel(), LifeCycleAware {

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

    init {
        viewModelScope.launch {
            when (staticPreferences.isDeviceRegistered) {
                true -> getLocationAndSave(appContext)
                false -> _isReady.emit(ReadyState.Ready)
            }
        }

        getConfig()
    }

    private fun getConfig() = viewModelScope.launch {
        getConfigUseCase().onSuccess { result ->
            appPreferences.setReferralLink(result.setting.inviteLinkForFriend)
            appPreferences.setBecomeDrive(result.setting.executorLink)
            appPreferences.setInviteFriends(result.setting.inviteLinkForFriend)
            appPreferences.setCardEnabled(result.setting.isCardEnabled)
            appPreferences.setSupportNumber(result.setting.supportPhone)

            if (result.setting.isCardEnabled)
                appPreferences.setPaymentType(PaymentType.CASH)
        }
    }

    private fun getLocationAndSave(context: Context) = viewModelScope.launch {
        repeat(MAX_LOCATION_ATTEMPTS) {
            val coordinates = withTimeoutOrNull(DEFAULT_LOCATION_TIMEOUT) {
                fetchLocationOnce(context)
            }

            if (coordinates != null) {
                appPreferences.setEntryLocation(lat = coordinates.first, lng = coordinates.second)
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

    override fun onAppear() {
        viewModelScope.launch { refreshFCMTokenUseCase() }
    }

    override fun onDisappear() {

    }
}