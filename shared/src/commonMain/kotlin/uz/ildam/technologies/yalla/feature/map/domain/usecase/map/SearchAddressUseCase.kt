package uz.ildam.technologies.yalla.feature.map.domain.usecase.map

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel
import uz.ildam.technologies.yalla.feature.map.domain.repository.MapRepository

class SearchAddressUseCase(
    private val repository: MapRepository
) {
    suspend operator fun invoke(
        lat: Double,
        lng: Double,
        query: String
    ): Result<List<SearchForAddressItemModel>, DataError.Network> {
        return repository.searchForAddress(
            lat = lat,
            lng = lng,
            query = query
        )
    }
}