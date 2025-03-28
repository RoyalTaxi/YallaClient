package uz.yalla.client.feature.profile.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.profile.domain.repository.LogoutRepository

class LogoutUseCase(
    private val repository: LogoutRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Result<Unit> {
        return withContext(dispatcher) {
            when (val result = repository.logout()) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(Unit)
            }
        }
    }
}