package uz.ildam.technologies.yalla.feature.map.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.map.data.request.map.GetRoutingRequestItem
import uz.ildam.technologies.yalla.feature.map.domain.model.response.map.AddressModel
import uz.ildam.technologies.yalla.feature.map.domain.model.response.map.GetRoutingModel
import uz.ildam.technologies.yalla.feature.map.domain.model.response.map.PolygonRemoteItem
import uz.ildam.technologies.yalla.feature.map.domain.model.response.map.SearchForAddressItemModel

interface MapRepository {
    suspend fun getPolygon(): Either<List<PolygonRemoteItem>, DataError.Network>

    suspend fun getAddress(lat: Double, lng: Double): Either<AddressModel, DataError.Network>

    suspend fun searchForAddress(
        lat: Double, lng: Double, query: String
    ): Either<List<SearchForAddressItemModel>, DataError.Network>

    suspend fun getRouting(addresses: List<GetRoutingRequestItem>): Either<GetRoutingModel, DataError.Network>
}