package uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.SendAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.repository.AuthRepository


class SendAuthCodeUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(number: String): Result<SendAuthCodeModel, DataError.Network> {
        return repository.sendAuthCode(number)
    }
}