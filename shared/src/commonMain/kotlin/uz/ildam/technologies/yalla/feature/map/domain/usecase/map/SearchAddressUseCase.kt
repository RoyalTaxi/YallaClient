package uz.ildam.technologies.yalla.feature.map.domain.usecase.map

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel
import uz.ildam.technologies.yalla.feature.map.domain.repository.MapRepository

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
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}