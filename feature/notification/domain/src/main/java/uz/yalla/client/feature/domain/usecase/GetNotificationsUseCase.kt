package uz.yalla.client.feature.domain.usecase

import app.cash.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import uz.yalla.client.feature.domain.model.NotificationModel
import uz.yalla.client.feature.domain.repository.NotificationRepository

class GetNotificationsUseCase(
    private val repository: NotificationRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    operator fun invoke(): Flow<PagingData<NotificationModel>> =
        repository
            .getNotifications()
            .flowOn(dispatcher)

}