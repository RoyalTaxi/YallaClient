package uz.yalla.client.feature.map.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.map.domain.model.response.SearchForAddressItemModel
import uz.yalla.client.feature.map.domain.repository.MapRepository

class SearchAddressUseCase(
    private val repository: MapRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(
        lat: Double,
        lng: Double,
        query: String
    ): Result<List<SearchForAddressItemModel>> {
        return withContext(dispatcher) {
            when (val result = repository.searchForAddress(
                lat = lat,
                lng = lng,
                query = query
            )) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}