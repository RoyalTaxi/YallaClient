package uz.ildam.technologies.yalla.feature.map.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.ildam.technologies.yalla.core.data.exception.safeApiCall
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.map.data.request.map.LocationNameRequest
import uz.ildam.technologies.yalla.feature.map.data.request.map.SearchForAddressRequest
import uz.ildam.technologies.yalla.feature.map.data.response.map.AddressNameResponse
import uz.ildam.technologies.yalla.feature.map.data.response.map.PolygonResponseItem
import uz.ildam.technologies.yalla.feature.map.data.response.map.SearchForAddressResponseItem
import uz.ildam.technologies.yalla.feature.map.data.url.MapUrl

class MapService(
    private val ktorWithApi2: HttpClient
) {
    suspend fun getPolygons(): Result<ApiResponseWrapper<List<PolygonResponseItem>>, DataError.Network> =
        safeApiCall {
            ktorWithApi2.get(MapUrl.POLYGON).body()
        }

    suspend fun getAddress(
        body: LocationNameRequest
    ): Result<ApiResponseWrapper<AddressNameResponse>, DataError.Network> = safeApiCall {
        ktorWithApi2.post(MapUrl.ADDRESS) { setBody(body) }.body()
    }

    suspend fun searchForAddress(
        body: SearchForAddressRequest
    ): Result<ApiResponseWrapper<List<SearchForAddressResponseItem>>, DataError.Network> =
        safeApiCall {
            ktorWithApi2.post(MapUrl.SEARCH) { setBody(body) }.body()
        }
}