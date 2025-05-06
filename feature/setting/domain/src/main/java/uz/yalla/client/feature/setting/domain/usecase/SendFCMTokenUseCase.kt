package uz.yalla.client.feature.setting.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.setting.domain.repository.ConfigRepository

class SendFCMTokenUseCase(
    private val repository: ConfigRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(token: String): Result<Unit> {
        return withContext(dispatcher) {
            when (val result = repository.sendFCMToken(token)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}