package uz.yalla.client.feature.map.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.domain.model.SearchableAddress
import uz.yalla.client.feature.map.domain.repository.MapRepository

class GetSecondaryAddressedUseCase(
    private val repository: MapRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(
        lat: Double,
        lng: Double
    ): Result<List<SearchableAddress>> {
        return withContext(dispatcher) {
            when (val result = repository.searchSecondaryAddress(
                lat = lat,
                lng = lng
            )) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(
                    result.data.map {
                        SearchableAddress(
                            addressId = null,
                            addressName = it.name,
                            distance = it.distance,
                            type = it.type,
                            lat = it.lat,
                            lng = it.lng,
                            name = it.name
                        )
                    }
                )
            }
        }
    }
}