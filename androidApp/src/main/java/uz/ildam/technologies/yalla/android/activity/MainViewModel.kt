package uz.ildam.technologies.yalla.android.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.connectivity.ConnectivityObserver
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.feature.settings.domain.usecase.GetConfigUseCase

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

    init {
        getConfig()
    }

    private fun getConfig() = viewModelScope.launch {
        getConfigUseCase()
            .onSuccess {
                AppPreferences.referralLink = it.setting.inviteLinkForFriend
                AppPreferences.becomeDrive = it.setting.executorLink
                AppPreferences.inviteFriends = it.setting.inviteLinkForFriend
            }
    }
}