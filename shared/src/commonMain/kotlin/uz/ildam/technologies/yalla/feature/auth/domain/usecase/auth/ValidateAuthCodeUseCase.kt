package uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import uz.ildam.technologies.yalla.core.domain.global.UseCaseWithTwoParams
import uz.ildam.technologies.yalla.feature.auth.domain.model.ValidateAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.repository.AuthRepository

class ValidateAuthCodeUseCase(
    private val repository: AuthRepository
) : UseCaseWithTwoParams<String, Int, ValidateAuthCodeModel>(Dispatchers.IO) {
    override suspend fun execute(parameter1: String, parameter2: Int): ValidateAuthCodeModel {
        return repository.validateAuthCode(parameter1, parameter2)
    }
}