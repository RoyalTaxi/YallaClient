package uz.yalla.client.feature.setting.data.repository

import uz.yalla.client.feature.setting.data.mapper.SettingsMapper
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.setting.domain.model.SettingsModel
import uz.yalla.client.feature.setting.domain.repository.SettingRepository
import uz.yalla.client.service.setting.request.SendFCMTokenRequest
import uz.yalla.client.service.setting.service.SettingsService

class SettingRepositoryImpl(
    private val service: SettingsService
) : SettingRepository {
    override suspend fun getConfig(): Either<SettingsModel, DataError.Network> {
        return when (val result = service.getConfig()) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(SettingsMapper.mapper))
        }
    }

    override suspend fun sendFCMToken(token: String): Either<Unit, DataError.Network> {
        return when (val result = service.sendFCMToken(SendFCMTokenRequest(token))) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(Unit)
        }
    }
}