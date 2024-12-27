package uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.VerifyAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.repository.AuthRepository

class VerifyCodeUseCase(
    private val repository: AuthRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(number: String, code: Int): Result<VerifyAuthCodeModel> {
        return withContext(dispatcher) {
            when (val result = repository.validateAuthCode(number, code)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}