package uz.yalla.client.feature.order.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.order.domain.model.request.PostOnePlaceDto
import uz.yalla.client.feature.order.domain.repository.PlacesRepository

class UpdateOnePlaceUseCase(
    private val repository: PlacesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(id: Int, body: PostOnePlaceDto): Result<Unit> {
        return withContext(dispatcher) {
            when (val result = repository.updateOne(id, body)) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(Unit)
            }
        }
    }
}