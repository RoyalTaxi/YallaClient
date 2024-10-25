package uz.ildam.technologies.yalla.feature.auth.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.auth.data.mapper.AuthMapper
import uz.ildam.technologies.yalla.feature.auth.data.request.auth.SendAuthCodeRequest
import uz.ildam.technologies.yalla.feature.auth.data.request.auth.ValidateAuthCodeRequest
import uz.ildam.technologies.yalla.feature.auth.data.service.AuthApiService
import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.SendAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.VerifyAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val service: AuthApiService
) : AuthRepository {

    override suspend fun sendAuthCode(
        number: String
    ): Result<SendAuthCodeModel, DataError.Network> {
        return when (val result = service.sendAuthCode(SendAuthCodeRequest(number))) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(result.data.result.let(AuthMapper.sendAuthCodeMapper))
        }
    }


    override suspend fun validateAuthCode(
        number: String,
        code: Int
    ): Result<VerifyAuthCodeModel, DataError.Network> {
        return when (val result = service.validateAuthCode(ValidateAuthCodeRequest(number, code))) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(result.data.result.let(AuthMapper.validateAuthCodeMapper))
        }
    }
}