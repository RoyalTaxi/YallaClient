package uz.yalla.client.service.map.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall
import uz.yalla.client.service.map.request.GetRoutingRequestItem
import uz.yalla.client.service.map.request.LocationNameRequest
import uz.yalla.client.service.map.request.SearchForAddressRequest
import uz.yalla.client.service.map.request.SecondaryAddressesRequest
import uz.yalla.client.service.map.response.PlaceNameResponse
import uz.yalla.client.service.map.response.GetRoutingResponse
import uz.yalla.client.service.map.response.PolygonResponseItem
import uz.yalla.client.service.map.response.SearchForAddressResponseItem
import uz.yalla.client.service.map.response.SecondaryAddressResponseItem
import uz.yalla.client.service.map.url.MapUrl


class MapService(
    private val ktorApi2: HttpClient
) {
    suspend fun getPolygons(): Either<ApiResponseWrapper<List<PolygonResponseItem>>, DataError.Network> =
        safeApiCall(isIdempotent = true) {
            ktorApi2.get(MapUrl.POLYGON)
        }

    suspend fun getAddress(
        body: LocationNameRequest
    ): Either<ApiResponseWrapper<PlaceNameResponse>, DataError.Network> = safeApiCall {
        ktorApi2.post(MapUrl.ADDRESS) { setBody(body) }
    }

    suspend fun searchForAddress(
        body: SearchForAddressRequest
    ): Either<ApiResponseWrapper<List<SearchForAddressResponseItem>>, DataError.Network> =
        safeApiCall {
            ktorApi2.post(MapUrl.SEARCH) { setBody(body) }
        }

    suspend fun getRouting(body: List<GetRoutingRequestItem>): Either<ApiResponseWrapper<GetRoutingResponse>, DataError.Network> =
        safeApiCall {
            ktorApi2.post(MapUrl.ROUTING) { setBody(body) }
        }

    suspend fun getSecondaryAddressed(
        body: SecondaryAddressesRequest
    ): Either<ApiResponseWrapper<List<SecondaryAddressResponseItem>>, DataError.Network> =
        safeApiCall {
            ktorApi2.post(MapUrl.SECONDARY_ADDRESSES) { setBody(body) }
        }
}
