package uz.yalla.client.feature.notification.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.domain.model.NotificationModel
import uz.yalla.client.feature.domain.repository.NotificationRepository
import uz.yalla.client.feature.notification.data.mapper.NotificationMapper
import uz.yalla.client.feature.notification.data.paging.NotificationPagingSource
import uz.yalla.client.service.notification.service.NotificationsApiService

class NotificationRepositoryImpl(
    private val service: NotificationsApiService
) : NotificationRepository {
    override fun getNotifications(): Flow<PagingData<NotificationModel>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { NotificationPagingSource(service) }
    ).flow

    override suspend fun getNotification(id: Int): Either<NotificationModel, DataError.Network> {
        return when (val result = service.getNotification(id)) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let ( NotificationMapper.mapper ))
        }
    }

    override suspend fun getNotificationsCount(): Either<Int, DataError.Network> {
        return when (val result = service.getNotificationCount()) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.or0())
        }
    }
}