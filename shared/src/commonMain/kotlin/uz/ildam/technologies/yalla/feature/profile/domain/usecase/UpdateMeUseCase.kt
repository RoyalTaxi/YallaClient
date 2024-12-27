package uz.ildam.technologies.yalla.feature.profile.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.core.domain.model.ClientModel
import uz.ildam.technologies.yalla.feature.profile.domain.model.request.UpdateMeDto
import uz.ildam.technologies.yalla.feature.profile.domain.repository.ProfileRepository

class UpdateMeUseCase(
    private val repository: ProfileRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(body: UpdateMeDto): Result<ClientModel> {
        return withContext(dispatcher) {
            when (val result = repository.updateMe(body)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}