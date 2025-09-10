package uz.yalla.client.feature.home.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.data.ext.mapResult
import uz.yalla.client.feature.home.data.mapper.HomeMapper
import uz.yalla.client.feature.home.domain.model.request.GetRoutingRequestItemDto
import uz.yalla.client.feature.home.domain.model.response.GetRoutingModel
import uz.yalla.client.feature.home.domain.model.response.PolygonRemoteItem
import uz.yalla.client.feature.home.domain.model.response.SearchForAddressItemModel
import uz.yalla.client.feature.home.domain.model.response.SecondaryAddressItemModel
import uz.yalla.client.feature.home.domain.repository.HomeRepository
import uz.yalla.client.feature.order.domain.model.response.PlaceNameModel
import uz.yalla.client.service.map.request.GetRoutingRequestItem
import uz.yalla.client.service.map.request.LocationNameRequest
import uz.yalla.client.service.map.request.SearchForAddressRequest
import uz.yalla.client.service.map.request.SecondaryAddressesRequest
import uz.yalla.client.service.map.service.MapService

class HomeRepositoryImpl(
    private val service: MapService
) : HomeRepository {
    override suspend fun getPolygon(): Either<List<PolygonRemoteItem>, DataError.Network> {
        return service.getPolygons().mapResult { it?.map(HomeMapper.polygonMapper).orEmpty() }
    }

    override suspend fun getAddress(
        lat: Double,
        lng: Double
    ): Either<PlaceNameModel, DataError.Network> {
        return when (val result = service.getAddress(LocationNameRequest(lat, lng))) {
            is Either.Error -> Either.Error(DataError.Network.UNKNOWN_ERROR)
            is Either.Success -> {
                if (result.data.result?.display_name.isNullOrBlank()) Either.Error(DataError.Network.UNKNOWN_ERROR)
                else Either.Success(result.data.result.let(HomeMapper.addressMapper))
            }
        }
    }

    override suspend fun searchForAddress(
        lat: Double,
        lng: Double,
        query: String
    ): Either<List<SearchForAddressItemModel>, DataError.Network> {
        return service.searchForAddress(
            SearchForAddressRequest(
                lat = lat,
                lng = lng,
                q = query
            )
        ).mapResult { it?.map(HomeMapper.searchAddressItemMapper).orEmpty() }
    }

    override suspend fun getRouting(addresses: List<GetRoutingRequestItemDto>): Either<GetRoutingModel, DataError.Network> {
        return service.getRouting(
            addresses.map {
                GetRoutingRequestItem(
                    type = it.type,
                    lng = it.lng,
                    lat = it.lat
                )
            }
        ).mapResult(HomeMapper.routingMapper)
    }

    override suspend fun searchSecondaryAddress(
        lat: Double,
        lng: Double
    ): Either<List<SecondaryAddressItemModel>, DataError.Network> {
        return service.getSecondaryAddressed(
            SecondaryAddressesRequest(
                lat = lat,
                lng = lng
            )
        ).mapResult { it?.map(HomeMapper.secondaryAddressItemMapper).orEmpty() }
    }
}
