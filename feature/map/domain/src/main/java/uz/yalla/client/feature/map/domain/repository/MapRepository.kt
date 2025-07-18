package uz.yalla.client.feature.map.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.map.domain.model.request.GetRoutingRequestItemDto
import uz.yalla.client.feature.map.domain.model.response.GetRoutingModel
import uz.yalla.client.feature.map.domain.model.response.PolygonRemoteItem
import uz.yalla.client.feature.map.domain.model.response.SearchForAddressItemModel
import uz.yalla.client.feature.map.domain.model.response.SecondaryAddressItemModel
import uz.yalla.client.feature.order.domain.model.response.PlaceNameModel

interface MapRepository {
    suspend fun getPolygon(): Either<List<PolygonRemoteItem>, DataError.Network>

    suspend fun getAddress(lat: Double, lng: Double): Either<PlaceNameModel, DataError.Network>

    suspend fun searchForAddress(
        lat: Double, lng: Double, query: String
    ): Either<List<SearchForAddressItemModel>, DataError.Network>

    suspend fun getRouting(addresses: List<GetRoutingRequestItemDto>): Either<GetRoutingModel, DataError.Network>

    suspend fun searchSecondaryAddress(
        lat: Double, lng: Double
    ): Either<List<SecondaryAddressItemModel>, DataError.Network>
}