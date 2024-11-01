package uz.ildam.technologies.yalla.feature.auth.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
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
    ): Result<RegisterModel, DataError.Network> {
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
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(result.data.result.let(RegisterMapper.mapper))
        }
    }
}