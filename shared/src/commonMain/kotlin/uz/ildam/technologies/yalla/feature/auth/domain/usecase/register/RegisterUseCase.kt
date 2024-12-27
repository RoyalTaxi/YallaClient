package uz.ildam.technologies.yalla.feature.auth.domain.usecase.register

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.auth.domain.model.register.RegisterModel
import uz.ildam.technologies.yalla.feature.auth.domain.repository.RegisterRepository

class RegisterUseCase(
    private val repository: RegisterRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(
        phone: String,
        firstName: String,
        lastName: String,
        gender: String,
        dateOfBirth: String,
        key: String
    ): Result<RegisterModel> {
        return withContext(dispatcher) {
            when (val result = repository.register(
                phone = phone,
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                dateOfBirth = dateOfBirth,
                key = key
            )) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}