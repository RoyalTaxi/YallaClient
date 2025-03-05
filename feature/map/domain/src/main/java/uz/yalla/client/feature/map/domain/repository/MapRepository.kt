package uz.yalla.client.feature.map.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.domain.model.PlaceModel
import uz.yalla.client.core.domain.model.PlaceNameModel
import uz.yalla.client.feature.map.domain.model.request.GetRoutingRequestItemDto
import uz.yalla.client.feature.map.domain.model.response.GetRoutingModel
import uz.yalla.client.feature.map.domain.model.response.PolygonRemoteItem
import uz.yalla.client.feature.map.domain.model.response.SearchForAddressItemModel

interface MapRepository {
    suspend fun getPolygon(): Either<List<PolygonRemoteItem>, DataError.Network>

    suspend fun getAddress(lat: Double, lng: Double): Either<PlaceNameModel, DataError.Network>

    suspend fun searchForAddress(
        lat: Double, lng: Double, query: String
    ): Either<List<SearchForAddressItemModel>, DataError.Network>

    suspend fun getRouting(addresses: List<GetRoutingRequestItemDto>): Either<GetRoutingModel, DataError.Network>
}