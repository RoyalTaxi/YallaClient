package uz.yalla.client.feature.setting.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.feature.setting.domain.repository.ConfigRepository

class RefreshFCMTokenUseCase(
    private val repository: ConfigRepository,
    private val appPreferences: AppPreferences,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Either<Unit, DataError.Network> {
        return withContext(dispatcher) {
            when (
                val getTokenResult = repository.getAndSaveFirebaseToken()
            ) {
                is Either.Error -> Either.Error(getTokenResult.error)

                is Either.Success -> {
                    val firebaseToken = getTokenResult.data
                    val accessToken = appPreferences.accessToken.firstOrNull()

                    if (!accessToken.isNullOrBlank())
                        repository.sendFCMTokenToBackend(firebaseToken, accessToken)
                    else
                        Either.Success(Unit)
                }
            }
        }
    }
}