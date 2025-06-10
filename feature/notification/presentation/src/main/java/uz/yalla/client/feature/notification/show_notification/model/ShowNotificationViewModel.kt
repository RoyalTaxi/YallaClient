package uz.yalla.client.feature.notification.show_notification.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.feature.domain.usecase.GetNotificationUseCase
import uz.yalla.client.feature.notification.show_notification.view.ShowNotificationUIState

internal class ShowNotificationViewModel(
    private val getNotificationUseCase: GetNotificationUseCase
) : BaseViewModel() {

    private val _stateFlow = MutableStateFlow(ShowNotificationUIState())
    val stateFlow = _stateFlow.asStateFlow()

    fun getNotification(id: Int) = viewModelScope.launchWithLoading {
        getNotificationUseCase(id)
            .onSuccess { result ->
                _stateFlow.update { it.copy(notification = result) }
            }
            .onFailure(::handleException)
    }
}