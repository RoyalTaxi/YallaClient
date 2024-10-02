package uz.ildam.technologies.yalla.feature.auth.data.repository

import uz.ildam.technologies.yalla.feature.auth.data.mapper.AuthMapper
import uz.ildam.technologies.yalla.feature.auth.data.request.SendAuthCodeRequest
import uz.ildam.technologies.yalla.feature.auth.data.request.ValidateAuthCodeRequest
import uz.ildam.technologies.yalla.feature.auth.data.service.AuthApiService
import uz.ildam.technologies.yalla.feature.auth.domain.model.SendAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.model.ValidateAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val service: AuthApiService
) : AuthRepository {
    override suspend fun sendAuthCode(number: String): SendAuthCodeModel {
        return service
            .sendAuthCode(SendAuthCodeRequest(number))
            .result
            .let(AuthMapper.sendAuthCodeMapper)
    }


    override suspend fun validateAuthCode(number: String, code: Int): ValidateAuthCodeModel {
        return service
            .validateAuthCode(ValidateAuthCodeRequest(number, code))
            .result
            .let(AuthMapper.validateAuthCodeMapper)
    }

}