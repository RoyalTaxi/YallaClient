package uz.yalla.client.feature.order.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.order.domain.repository.PlacesRepository

class DeleteOnePlaceUseCase(
    private val repository: PlacesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return withContext(dispatcher) {
            when (val result = repository.deleteOne(id)) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(Unit)
            }
        }
    }
}