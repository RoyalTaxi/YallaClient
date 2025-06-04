package uz.yalla.client.feature.setting.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.setting.domain.model.ConfigModel
import uz.yalla.client.feature.setting.domain.repository.ConfigRepository

class GetConfigUseCase(
    private val repository: ConfigRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(): Result<ConfigModel> {
        return withContext(dispatcher) {
            when (val result = repository.getConfig()) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}