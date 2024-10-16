package uz.ildam.technologies.yalla.feature.auth.data.repository

import io.ktor.client.plugins.ResponseException
import uz.ildam.technologies.yalla.core.domain.model.DataError
import uz.ildam.technologies.yalla.core.domain.model.Result
import uz.ildam.technologies.yalla.feature.auth.data.mapper.AuthMapper
import uz.ildam.technologies.yalla.feature.auth.data.request.auth.SendAuthCodeRequest
import uz.ildam.technologies.yalla.feature.auth.data.request.auth.ValidateAuthCodeRequest
import uz.ildam.technologies.yalla.feature.auth.data.service.AuthApiService
import uz.ildam.technologies.yalla.feature.auth.domain.model.SendAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.model.VerifyAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val service: AuthApiService
) : AuthRepository {
    
    override suspend fun sendAuthCode(
        number: String
    ): Result<SendAuthCodeModel, DataError.Network> {
        return try {
            Result.Success(
                service
                    .sendAuthCode(SendAuthCodeRequest(number))
                    .result
                    .let(AuthMapper.sendAuthCodeMapper)
            )
        } catch (e: ResponseException) {
            Result.Error(DataError.Network.SERVER_ERROR)
        }
    }


    override suspend fun validateAuthCode(
        number: String,
        code: Int
    ): Result<VerifyAuthCodeModel, DataError.Network> {
        return try {
            Result.Success(
                service
                    .validateAuthCode(ValidateAuthCodeRequest(number, code))
                    .result
                    .let(AuthMapper.validateAuthCodeMapper)
            )
        } catch (e: ResponseException) {
            Result.Error(DataError.Network.SERVER_ERROR)
        }
    }
}