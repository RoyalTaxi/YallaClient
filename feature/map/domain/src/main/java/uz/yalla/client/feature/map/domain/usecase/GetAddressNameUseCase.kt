package uz.yalla.client.feature.map.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.map.domain.repository.MapRepository
import uz.yalla.client.feature.order.domain.model.response.PlaceNameModel

class GetAddressNameUseCase(
    private val repository: MapRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(lat: Double, lng: Double): Result<PlaceNameModel> {
        return withContext(dispatcher) {
            when (val result = repository.getAddress(lat, lng)) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}