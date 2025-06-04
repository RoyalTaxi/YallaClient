package uz.yalla.client.feature.profile.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.profile.domain.model.response.GetMeModel
import uz.yalla.client.feature.profile.domain.repository.ProfileRepository

class GetMeUseCase(
    private val repository: ProfileRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Result<GetMeModel> {
        return withContext(dispatcher) {
            when (val result = repository.getMe()) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}