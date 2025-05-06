package uz.yalla.client.feature.setting.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.setting.data.mapper.ConfigMapper
import uz.yalla.client.feature.setting.domain.model.ConfigModel
import uz.yalla.client.feature.setting.domain.repository.ConfigRepository
import uz.yalla.client.service.setting.request.SendFCMTokenRequest
import uz.yalla.client.service.setting.service.ConfigService

class ConfigRepositoryImpl(
    private val service: ConfigService
) : ConfigRepository {
    override suspend fun getConfig(): Either<ConfigModel, DataError.Network> {
        return when (val result = service.getConfig()) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(ConfigMapper.mapper))
        }
    }

    override suspend fun sendFCMToken(token: String): Either<Unit, DataError.Network> {
        return when (val result = service.sendFCMToken(SendFCMTokenRequest(token))) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(Unit)
        }
    }
}