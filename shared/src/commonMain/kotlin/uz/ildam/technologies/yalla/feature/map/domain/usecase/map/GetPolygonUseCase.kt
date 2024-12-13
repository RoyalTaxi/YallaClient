package uz.ildam.technologies.yalla.feature.map.domain.usecase.map

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.map.domain.model.map.PolygonRemoteItem
import uz.ildam.technologies.yalla.feature.map.domain.repository.MapRepository

class GetPolygonUseCase(
    private val repository: MapRepository
) {
    suspend operator fun invoke(): Result<List<PolygonRemoteItem>, DataError.Network> =
        repository.getPolygon()
}