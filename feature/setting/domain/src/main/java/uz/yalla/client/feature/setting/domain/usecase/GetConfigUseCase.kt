package uz.yalla.client.feature.setting.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.setting.domain.model.SettingsModel
import uz.yalla.client.feature.setting.domain.repository.SettingRepository

class GetConfigUseCase(
    private val repository: SettingRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(): Result<SettingsModel> {
        return withContext(dispatcher) {
            when (val result = repository.getConfig()) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}