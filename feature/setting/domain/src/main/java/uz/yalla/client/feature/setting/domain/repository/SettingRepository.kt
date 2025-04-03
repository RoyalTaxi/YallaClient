package uz.yalla.client.feature.setting.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.setting.domain.model.SettingsModel

interface SettingRepository {
    suspend fun getConfig(): Either<SettingsModel, DataError.Network>
    suspend fun sendFCMToken(token: String): Either<Unit, DataError.Network>
}