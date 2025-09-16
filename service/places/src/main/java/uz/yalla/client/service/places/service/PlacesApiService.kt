package uz.yalla.client.service.places.service

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.appendPathSegments
import uz.yalla.client.service.places.url.PlacesUrl
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall
import uz.yalla.client.service.places.request.PostOnePlaceRequest
import uz.yalla.client.service.places.response.PlaceRemoteModel

class PlacesApiService(
    private val ktorApi1: HttpClient
) {
    suspend fun findAll(): Either<ApiResponseWrapper<List<PlaceRemoteModel>>, DataError.Network> =
        safeApiCall(isIdempotent = true) { ktorApi1.get(PlacesUrl.FIND_ALL) }

    suspend fun findOne(id: Int): Either<ApiResponseWrapper<PlaceRemoteModel>, DataError.Network> =
        safeApiCall(isIdempotent = true) {
            ktorApi1.get(PlacesUrl.FIND_ONE) { url { appendPathSegments(id.toString()) } }
        }

    suspend fun postOne(body: PostOnePlaceRequest): Either<ApiResponseWrapper<List<String>>, DataError.Network> =
        safeApiCall { ktorApi1.post(PlacesUrl.POST_ONE) { setBody(body) } }

    suspend fun deleteOne(id: Int): Either<Unit, DataError.Network> =
        safeApiCall {
            ktorApi1.delete(PlacesUrl.DELETE) { url { appendPathSegments(id.toString()) } }
        }

    suspend fun updateOne(id: Int, body: PostOnePlaceRequest): Either<Unit, DataError.Network> =
        safeApiCall {
            ktorApi1.put(PlacesUrl.UPDATE) {
                url { appendPathSegments(id.toString()) }
                setBody(body)
            }
        }
}
