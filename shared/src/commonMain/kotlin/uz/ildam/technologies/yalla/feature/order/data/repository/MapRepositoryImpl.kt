package uz.ildam.technologies.yalla.feature.order.data.repository

import io.ktor.client.plugins.ResponseException
import uz.ildam.technologies.yalla.feature.order.data.mapper.MapMapper
import uz.ildam.technologies.yalla.feature.order.data.service.MapService
import uz.ildam.technologies.yalla.feature.order.domain.model.map.PolygonRemoteItem
import uz.ildam.technologies.yalla.feature.order.domain.repository.MapRepository
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result

class MapRepositoryImpl(
    private val service: MapService
) : MapRepository {
    override suspend fun getPolygon(): Result<List<PolygonRemoteItem>, DataError.Network> {
        return try {
            Result.Success(service.getPolygons().result?.map(MapMapper.mapper).orEmpty())
        } catch (e: Exception) {
            println("hello ${e.message}")
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}