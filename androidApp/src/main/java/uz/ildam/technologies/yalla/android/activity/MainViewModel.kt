package uz.ildam.technologies.yalla.android.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import uz.ildam.technologies.yalla.android.connectivity.ConnectivityObserver

class MainViewModel(
    connectivityObserver: ConnectivityObserver
) : ViewModel() {
    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            true
        )
}