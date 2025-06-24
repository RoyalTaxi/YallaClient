package uz.yalla.client.feature.auth.data.repository

import uz.yalla.client.feature.auth.data.mapper.AuthMapper
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.auth.domain.model.auth.SendAuthCodeModel
import uz.yalla.client.feature.auth.domain.model.auth.VerifyAuthCodeModel
import uz.yalla.client.feature.auth.domain.repository.AuthRepository
import uz.yalla.client.service.auth.request.auth.SendAuthCodeRequest
import uz.yalla.client.service.auth.request.auth.ValidateAuthCodeRequest
import uz.yalla.client.service.auth.service.AuthApiService

class AuthRepositoryImpl(
    private val service: AuthApiService
) : AuthRepository {
    override suspend fun sendAuthCode(
        number: String,
        hash: String?
    ): Either<SendAuthCodeModel, DataError.Network> {
        return when (val result = service.sendAuthCode(SendAuthCodeRequest(number, hash))) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(AuthMapper.sendAuthCodeMapper))
        }
    }

    override suspend fun validateAuthCode(
        number: String,
        code: String
    ): Either<VerifyAuthCodeModel, DataError.Network> {
        return when (val result = service.validateAuthCode(ValidateAuthCodeRequest(number, code))) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(AuthMapper.validateAuthCodeMapper))
        }
    }
}