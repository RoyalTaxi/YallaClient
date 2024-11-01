package uz.ildam.technologies.yalla.feature.map.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import uz.ildam.technologies.yalla.core.data.exception.safeApiCall
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.feature.map.data.response.address.AddressResponse
import uz.ildam.technologies.yalla.feature.map.data.response.address.PolygonResponseItem
import uz.ildam.technologies.yalla.feature.map.data.url.MapUrl

class MapService(
    private val ktorWithApi1: HttpClient,
    private val ktorWithApi2: HttpClient
) {
    suspend fun getPolygons(): Result<ApiResponseWrapper<List<PolygonResponseItem>>, DataError.Network> =
        safeApiCall {
            ktorWithApi2.get(MapUrl.POLYGON).body()
        }

    suspend fun getAddress(
        lat: Double, lng: Double,
    ): Result<ApiResponseWrapper<AddressResponse>, DataError.Network> = safeApiCall {
        ktorWithApi1.get {
            url(MapUrl.ADDRESS)
            parameter("lat", lat)
            parameter("lng", lng)
        }.body()
    }
}