package uz.yalla.client.feature.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.domain.repository.NotificationRepository

class GetNotificationsCountUseCase(
    private val repository: NotificationRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Result<Int> {
        return withContext(dispatcher) {
            when (val result = repository.getNotificationsCount()) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}