package uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth

import uz.ildam.technologies.yalla.core.domain.model.DataError
import uz.ildam.technologies.yalla.core.domain.model.Result
import uz.ildam.technologies.yalla.feature.auth.domain.model.VerifyAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.repository.AuthRepository

class VerifyAuthCodeUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        number: String,
        code: Int
    ): Result<VerifyAuthCodeModel, DataError.Network> {
        return repository.validateAuthCode(number, code)
    }
}