package uz.yalla.client.feature.setting.data.repository

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.data.ext.mapResult
import uz.yalla.client.core.data.ext.mapSuccess
import uz.yalla.client.feature.setting.data.mapper.ConfigMapper
import uz.yalla.client.feature.setting.domain.model.ConfigModel
import uz.yalla.client.feature.setting.domain.repository.ConfigRepository
import uz.yalla.client.service.setting.request.SendFCMTokenRequest
import uz.yalla.client.service.setting.service.ConfigService

class ConfigRepositoryImpl(
    private val service: ConfigService,
    private val prefs: AppPreferences
) : ConfigRepository {
    override suspend fun getConfig(): Either<ConfigModel, DataError.Network> {
        return service.getConfig().mapResult(ConfigMapper.mapper)
    }

    override suspend fun getAndSaveFirebaseToken(): Either<String, DataError.Network> {
        return withContext(Dispatchers.IO) {
            val firebaseToken = FirebaseMessaging.getInstance().token.await()
            prefs.setFirebaseToken(firebaseToken)
            Either.Success(firebaseToken)
        }
    }

    override suspend fun sendFCMTokenToBackend(
        token: String,
        accessToken: String
    ): Either<Unit, DataError.Network> {
        return withContext(Dispatchers.IO) {
            service.sendFCMToken(SendFCMTokenRequest(token)).mapSuccess { Unit }
        }
    }
}
