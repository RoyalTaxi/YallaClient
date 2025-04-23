package uz.yalla.client.feature.domain.repository

import app.cash.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.domain.model.NotificationModel

interface NotificationRepository {
    fun getNotifications(): Flow<PagingData<NotificationModel>>
    suspend fun getNotification(id: Int): Either<NotificationModel, DataError.Network>
    suspend fun getNotificationsCount(): Either<Int, DataError.Network>
}