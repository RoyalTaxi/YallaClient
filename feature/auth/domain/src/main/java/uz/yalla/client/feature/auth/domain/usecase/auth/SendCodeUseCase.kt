package uz.yalla.client.feature.auth.domain.usecase.auth

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.auth.domain.model.auth.SendAuthCodeModel
import uz.yalla.client.feature.auth.domain.repository.AuthRepository

class SendCodeUseCase(
    private val repository: AuthRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(number: String, hash: String?): Result<SendAuthCodeModel> {
        return withContext(dispatcher) {
            when (val result = repository.sendAuthCode(number, hash)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}