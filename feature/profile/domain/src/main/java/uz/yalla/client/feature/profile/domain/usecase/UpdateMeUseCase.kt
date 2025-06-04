package uz.yalla.client.feature.profile.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.domain.model.Client
import uz.yalla.client.feature.profile.domain.model.request.UpdateMeDto
import uz.yalla.client.feature.profile.domain.repository.ProfileRepository

class UpdateMeUseCase(
    private val repository: ProfileRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(body: UpdateMeDto): Result<Client> {
        return withContext(dispatcher) {
            when (val result = repository.updateMe(body)) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}