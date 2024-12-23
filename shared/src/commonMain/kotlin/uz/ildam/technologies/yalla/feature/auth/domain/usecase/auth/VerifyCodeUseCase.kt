package uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.VerifyAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.repository.AuthRepository

class VerifyCodeUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        number: String,
        code: Int
    ): Result<VerifyAuthCodeModel, DataError.Network> {
        return repository.validateAuthCode(number, code)
    }
}