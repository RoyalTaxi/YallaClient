package uz.ildam.technologies.yalla.feature.map.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.map.data.mapper.MapMapper
import uz.ildam.technologies.yalla.feature.map.data.service.MapService
import uz.ildam.technologies.yalla.feature.map.domain.model.map.AddressModel
import uz.ildam.technologies.yalla.feature.map.domain.model.map.PolygonRemoteItem
import uz.ildam.technologies.yalla.feature.map.domain.repository.MapRepository

class MapRepositoryImpl(
    private val service: MapService
) : MapRepository {
    override suspend fun getPolygon(): Result<List<PolygonRemoteItem>, DataError.Network> {
        return when (val result = service.getPolygons()) {
            is Result.Error -> Result.Error(DataError.Network.UNKNOWN_ERROR)
            is Result.Success -> Result.Success(
                result.data.result?.map(MapMapper.polygonMapper).orEmpty()
            )
        }
    }

    override suspend fun getAddress(
        lat: Double,
        lng: Double
    ): Result<AddressModel, DataError.Network> {
        return when (val result = service.getAddress(lat, lng)) {
            is Result.Error -> {
                Result.Error(DataError.Network.UNKNOWN_ERROR)
            }

            is Result.Success -> Result.Success(result.data.result.let(MapMapper.addressMapper))
        }
    }
}