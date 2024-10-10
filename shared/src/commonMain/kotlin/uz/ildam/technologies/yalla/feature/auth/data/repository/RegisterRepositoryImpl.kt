package uz.ildam.technologies.yalla.feature.auth.data.repository

import uz.ildam.technologies.yalla.feature.auth.data.request.register.RegisterRequest
import uz.ildam.technologies.yalla.feature.auth.data.service.RegisterApiService
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
    ) {
        service.register(
            RegisterRequest(
                phone = phone,
                given_names = firstName,
                sur_name = lastName,
                gender = gender,
                birthday = dateOfBirth,
                key = key
            )
        )
    }
}