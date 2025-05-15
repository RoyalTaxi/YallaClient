package uz.yalla.client.feature.notification.show_notification.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.feature.domain.usecase.GetNotificationUseCase
import uz.yalla.client.feature.notification.show_notification.view.ShowNotificationUIState

internal class ShowNotificationViewModel(
    private val getNotificationUseCase: GetNotificationUseCase
) : ViewModel() {

    private val _stateFlow = MutableStateFlow(ShowNotificationUIState())
    val stateFlow = _stateFlow.asStateFlow()

    val loading = stateFlow
        .map {
            it.notification == null
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = true
        )

    fun getNotification(id: Int) = viewModelScope.launch {
        getNotificationUseCase(id)
            .onSuccess { result ->
                _stateFlow.update { it.copy(notification = result) }
            }
    }
}