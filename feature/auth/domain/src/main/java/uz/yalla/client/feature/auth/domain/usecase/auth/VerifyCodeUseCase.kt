package uz.yalla.client.feature.auth.domain.usecase.auth

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.auth.domain.model.auth.VerifyAuthCodeModel
import uz.yalla.client.feature.auth.domain.repository.AuthRepository

class VerifyCodeUseCase(
    private val repository: AuthRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(number: String, code: String): Result<VerifyAuthCodeModel> {
        return withContext(dispatcher) {
            when (val result = repository.validateAuthCode(number.getFormattedNumber(), code)) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}