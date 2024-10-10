package uz.ildam.technologies.yalla.feature.auth.domain.usecase.register

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import uz.ildam.technologies.yalla.core.domain.global.UseCaseWithSixParams
import uz.ildam.technologies.yalla.feature.auth.domain.repository.RegisterRepository

class RegisterUseCase(
    private val repository: RegisterRepository
) : UseCaseWithSixParams<String, String, String, String, String, String, Unit>(Dispatchers.IO) {
    override suspend fun execute(
        parameter1: String,
        parameter2: String,
        parameter3: String,
        parameter4: String,
        parameter5: String,
        parameter6: String
    ) {
        return repository.register(
            phone = parameter1,
            firstName = parameter2,
            lastName = parameter3,
            gender = parameter4,
            dateOfBirth = parameter5,
            key = parameter6
        )
    }
}