package uz.yalla.client.feature.setting.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.setting.domain.model.ConfigModel

interface ConfigRepository {
    suspend fun getConfig(): Either<ConfigModel, DataError.Network>

    suspend fun getAndSaveFirebaseToken(): Either<String, DataError.Network>

    suspend fun sendFCMTokenToBackend(
        token: String,
        accessToken: String
    ): Either<Unit, DataError.Network>
}