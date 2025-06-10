package uz.yalla.client.feature.notification.notifications.model

import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.feature.domain.model.NotificationModel
import uz.yalla.client.feature.domain.usecase.GetNotificationsUseCase

internal class NotificationsViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase
) : BaseViewModel() {

    private val _notifications = MutableSharedFlow<PagingData<NotificationModel>>()
    val notifications = _notifications.asSharedFlow()

    init {
        getNotifications()
    }

    fun getNotifications() = viewModelScope.launch {
        getNotificationsUseCase()
            .cachedIn(viewModelScope)
            .collectLatest {
                _notifications.emit(it)
            }
    }
}