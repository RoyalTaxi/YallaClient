package uz.ildam.technologies.yalla.feature.map.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
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
    override suspend fun getPolygon(): Either<List<PolygonRemoteItem>, DataError.Network> {
        return when (val result = service.getPolygons()) {
            is Either.Error -> Either.Error(DataError.Network.UNKNOWN_ERROR)
            is Either.Success -> Either.Success(
                result.data.result?.map(MapMapper.polygonMapper).orEmpty()
            )
        }
    }

    override suspend fun getAddress(
        lat: Double,
        lng: Double
    ): Either<AddressModel, DataError.Network> {
        return when (val result = service.getAddress(LocationNameRequest(lat, lng))) {
            is Either.Error -> Either.Error(DataError.Network.UNKNOWN_ERROR)
            is Either.Success -> {
                if (result.data.result?.display_name.isNullOrBlank()) Either.Error(DataError.Network.UNKNOWN_ERROR)
                else Either.Success(result.data.result.let(MapMapper.addressMapper))
            }
        }
    }

    override suspend fun searchForAddress(
        lat: Double,
        lng: Double,
        query: String
    ): Either<List<SearchForAddressItemModel>, DataError.Network> {

        return when (val result = service.searchForAddress(
            SearchForAddressRequest(
                lat = lat,
                lng = lng,
                q = query
            )
        )) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(
                result.data.result?.map(MapMapper.searchAddressItemMapper).orEmpty()
            )
        }
    }
}