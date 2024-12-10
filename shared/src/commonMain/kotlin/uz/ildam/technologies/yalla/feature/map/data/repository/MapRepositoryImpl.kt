package uz.ildam.technologies.yalla.feature.map.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.map.data.mapper.MapMapper
import uz.ildam.technologies.yalla.feature.map.data.request.map.LocationNameRequest
import uz.ildam.technologies.yalla.feature.map.data.request.map.SearchForAddressRequest
import uz.ildam.technologies.yalla.feature.map.data.service.MapService
import uz.ildam.technologies.yalla.feature.map.domain.model.map.AddressModel
import uz.ildam.technologies.yalla.feature.map.domain.model.map.PolygonRemoteItem
import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel
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
        return when (val result = service.getAddress(LocationNameRequest(lat, lng))) {
            is Result.Error -> Result.Error(DataError.Network.UNKNOWN_ERROR)
            is Result.Success -> {
                if (result.data.result?.display_name.isNullOrBlank()) Result.Error(DataError.Network.UNKNOWN_ERROR)
                else Result.Success(result.data.result.let(MapMapper.addressMapper))
            }
        }
    }

    override suspend fun searchForAddress(
        lat: Double,
        lng: Double,
        query: String
    ): Result<List<SearchForAddressItemModel>, DataError.Network> {

        return when (val result = service.searchForAddress(
            SearchForAddressRequest(
                lat = lat,
                lng = lng,
                q = query
            )
        )) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(
                result.data.result?.map(MapMapper.searchAddressItemMapper).orEmpty()
            )
        }
    }
}