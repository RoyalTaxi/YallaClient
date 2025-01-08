package uz.ildam.technologies.yalla.feature.settings.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.settings.data.mapper.SettingsMapper
import uz.ildam.technologies.yalla.feature.settings.data.service.SettingsService
import uz.ildam.technologies.yalla.feature.settings.domain.model.SettingsModel
import uz.ildam.technologies.yalla.feature.settings.domain.repository.SettingRepository

class SettingRepositoryImpl(
    private val service: SettingsService
) : SettingRepository {
    override suspend fun getConfig(): Either<SettingsModel, DataError.Network> {
        return when (val result = service.getConfig()) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(SettingsMapper.mapper))
        }
    }
}