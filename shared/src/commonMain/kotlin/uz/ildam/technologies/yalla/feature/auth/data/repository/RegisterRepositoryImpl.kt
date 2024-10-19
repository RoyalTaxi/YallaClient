package uz.ildam.technologies.yalla.feature.auth.data.repository

import io.ktor.client.plugins.ResponseException
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.auth.data.mapper.RegisterMapper
import uz.ildam.technologies.yalla.feature.auth.data.request.register.RegisterRequest
import uz.ildam.technologies.yalla.feature.auth.data.service.RegisterApiService
import uz.ildam.technologies.yalla.feature.auth.domain.model.register.RegisterModel
import uz.ildam.technologies.yalla.feature.auth.domain.repository.RegisterRepository

class RegisterRepositoryImpl(
    private val service: RegisterApiService
) : RegisterRepository {
    override suspend fun register(
        phone: String,
        firstName: String,
        lastName: String,
        gender: String,
        dateOfBirth: String,
        key: String
    ): Result<RegisterModel, DataError.Network> {
        return try {
            Result.Success(
                service
                    .register(
                        RegisterRequest(
                            phone = phone,
                            given_names = firstName,
                            sur_name = lastName,
                            gender = gender,
                            birthday = dateOfBirth,
                            key = key
                        )
                    )
                    .result
                    .let(RegisterMapper.mapper)
            )
        } catch (e: ResponseException) {
            Result.Error(DataError.Network.SERVER_ERROR)
        }
    }
}