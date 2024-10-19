package uz.ildam.technologies.yalla.feature.order.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.domain.model.map.PolygonRemoteItem

interface MapRepository {
    suspend fun getPolygon(): Result<List<PolygonRemoteItem>, DataError.Network>
}