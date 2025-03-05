package uz.yalla.client.feature.profile.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.profile.domain.model.response.UpdateAvatarModel
import uz.yalla.client.feature.profile.domain.repository.ProfileRepository

class UpdateAvatarUseCase(
    private val repository: ProfileRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(image: ByteArray): Result<UpdateAvatarModel> {
        return withContext(dispatcher) {
            when (val result = repository.updateAvatar(image)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}