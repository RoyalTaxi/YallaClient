package uz.ildam.technologies.yalla.feature.settings.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.settings.domain.model.SettingsModel
import uz.ildam.technologies.yalla.feature.settings.domain.repository.SettingRepository

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