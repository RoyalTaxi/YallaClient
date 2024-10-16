package uz.ildam.technologies.yalla.feature.auth.domain.usecase.register

import uz.ildam.technologies.yalla.core.domain.model.DataError
import uz.ildam.technologies.yalla.core.domain.model.Result
import uz.ildam.technologies.yalla.feature.auth.domain.repository.RegisterRepository

class RegisterUseCase(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(
        phone: String,
        firstName: String,
        lastName: String,
        gender: String,
        dateOfBirth: String,
        key: String
    ): Result<Unit, DataError.Network> {
        return repository.register(
            phone = phone,
            firstName = firstName,
            lastName = lastName,
            gender = gender,
            dateOfBirth = dateOfBirth,
            key = key
        )
    }
}