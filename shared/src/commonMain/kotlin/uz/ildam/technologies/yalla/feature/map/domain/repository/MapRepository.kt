package uz.ildam.technologies.yalla.feature.map.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.map.domain.model.map.AddressModel
import uz.ildam.technologies.yalla.feature.map.domain.model.map.PolygonRemoteItem
import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel

interface MapRepository {
    suspend fun getPolygon(): Result<List<PolygonRemoteItem>, DataError.Network>

    suspend fun getAddress(lat: Double, lng: Double): Result<AddressModel, DataError.Network>

    suspend fun searchForAddress(
        lat: Double, lng: Double, query: String
    ): Result<List<SearchForAddressItemModel>, DataError.Network>
}