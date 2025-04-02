package uz.yalla.client.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.yalla.client.connectivity.ConnectivityObserver
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.feature.setting.domain.usecase.GetConfigUseCase

class MainViewModel(
    connectivityObserver: ConnectivityObserver,
    private val getConfigUseCase: GetConfigUseCase
) : ViewModel() {
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
        getCurrentLocation(
            context = context,
            onLocationFetched = { location ->
                AppPreferences.entryLocation = Pair(location.latitude, location.longitude)
                this.launch(Dispatchers.Main) {
                    _isReady.emit(true)
                }
            },
            onPermissionDenied = {
                this.launch(Dispatchers.Main) {
                    _isReady.emit(false)
                }
            }
        )
    }
}