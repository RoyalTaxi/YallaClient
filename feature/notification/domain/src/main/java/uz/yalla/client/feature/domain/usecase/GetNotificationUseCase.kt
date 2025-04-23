package uz.yalla.client.feature.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.domain.model.NotificationModel
import uz.yalla.client.feature.domain.repository.NotificationRepository

class GetNotificationUseCase(
    private val repository: NotificationRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(id: Int): Result<NotificationModel> {
        return withContext(dispatcher) {
            when (val result = repository.getNotification(id)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}