package uz.yalla.client.service.places.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import uz.ildam.technologies.yalla.feature.addresses.data.url.PlacesUrl
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall
import uz.yalla.client.service.places.request.PostOnePlaceRequest
import uz.yalla.client.service.places.response.PlaceRemoteModel

class PlacesApiService(
    private val ktor: HttpClient
) {
    suspend fun findAll(): Either<ApiResponseWrapper<List<PlaceRemoteModel>>, DataError.Network> =
        safeApiCall { ktor.get(PlacesUrl.FIND_ALL).body() }

    suspend fun findOne(id: Int): Either<ApiResponseWrapper<PlaceRemoteModel>, DataError.Network> =
        safeApiCall { ktor.get(PlacesUrl.FIND_ONE + id).body() }

    suspend fun postOne(body: PostOnePlaceRequest): Either<ApiResponseWrapper<List<String>>, DataError.Network> =
        safeApiCall { ktor.post(PlacesUrl.POST_ONE) { setBody(body) }.body() }

    suspend fun deleteOne(id: Int): Either<Unit, DataError.Network> =
        safeApiCall { ktor.delete(PlacesUrl.DELETE + id).body() }

    suspend fun updateOne(id: Int, body: PostOnePlaceRequest): Either<Unit, DataError.Network> =
        safeApiCall { ktor.put(PlacesUrl.UPDATE + id) { setBody(body) }.body() }

    suspend fun findAllMapAddresses(): Either<ApiResponseWrapper<List<PlaceRemoteModel>>, DataError.Network> =
        safeApiCall { ktor.get(PlacesUrl.FIND_ALL_MAP_ADDRESSES).body() }
}