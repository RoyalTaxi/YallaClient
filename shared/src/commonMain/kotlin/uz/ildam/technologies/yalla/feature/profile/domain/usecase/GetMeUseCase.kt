package uz.ildam.technologies.yalla.feature.profile.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.profile.domain.model.response.GetMeModel
import uz.ildam.technologies.yalla.feature.profile.domain.repository.ProfileRepository

class GetMeUseCase(
    private val repository: ProfileRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Result<GetMeModel> {
        return withContext(dispatcher) {
            when (val result = repository.getMe()) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}