package uz.ildam.technologies.yalla.feature.order.domain.usecase.map

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.domain.model.map.PolygonRemoteItem
import uz.ildam.technologies.yalla.feature.order.domain.repository.MapRepository

class GetPolygonUseCase(
    private val repository: MapRepository
) {
    suspend operator fun invoke(): Result<List<PolygonRemoteItem>, DataError.Network> {
        return repository.getPolygon()
    }
}