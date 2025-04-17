package uz.yalla.client.feature.notification.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

internal class NotificationViewModel() :ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<NotificationActionState>()
    val actionState = _actionState.asSharedFlow()
}