package uz.ildam.technologies.yalla.feature.auth.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.auth.data.mapper.RegisterMapper
import uz.ildam.technologies.yalla.feature.auth.data.request.register.RegisterUserRequest
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
    ): Either<RegisterModel, DataError.Network> {
        return when (val result = service.register(
            RegisterUserRequest(
                phone = phone,
                given_names = firstName,
                sur_name = lastName,
                gender = gender,
                birthday = dateOfBirth,
                key = key
            )
        )) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(RegisterMapper.mapper))
        }
    }
}