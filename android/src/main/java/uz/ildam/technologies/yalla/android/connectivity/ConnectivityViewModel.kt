package uz.ildam.technologies.yalla.android.ui.screens.offline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import uz.ildam.technologies.yalla.android.connectivity.ConnectivityObserver

class OfflineViewModel(
    private val connectivityObserver: ConnectivityObserver
): ViewModel() {
    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )
}