package uz.yalla.client.feature.order.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.order.domain.model.request.PostOnePlaceDto
import uz.yalla.client.feature.order.domain.repository.PlacesRepository

class PostOnePlaceUseCase(
    private val repository: PlacesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(body: PostOnePlaceDto): Result<Unit> {
        return withContext(dispatcher) {
            when (val result = repository.postOne(body)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}