package uz.yalla.client.feature.order.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.feature.order.domain.repository.PlacesRepository
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.order.domain.model.response.PlaceModel

class FindAllMapPlacesUseCase(
    private val repository: PlacesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Result<List<PlaceModel>> {
        return withContext(dispatcher) {
            when (val result = repository.findAllMapAddresses()) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}