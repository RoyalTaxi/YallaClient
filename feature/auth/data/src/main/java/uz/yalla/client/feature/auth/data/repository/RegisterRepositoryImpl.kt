package uz.yalla.client.feature.auth.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.data.ext.mapResult
import uz.yalla.client.feature.auth.data.mapper.RegisterMapper
import uz.yalla.client.feature.auth.domain.model.register.RegisterModel
import uz.yalla.client.feature.auth.domain.repository.RegisterRepository
import uz.yalla.client.service.auth.request.register.RegisterUserRequest
import uz.yalla.client.service.auth.service.RegisterApiService

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
    ): Either<RegisterModel, DataError.Network> =
        service
            .register(
                RegisterUserRequest(
                    phone = phone,
                    given_names = firstName,
                    sur_name = lastName,
                    gender = gender,
                    birthday = dateOfBirth,
                    key = key
                )
            )
            .mapResult(RegisterMapper.mapper)
}
