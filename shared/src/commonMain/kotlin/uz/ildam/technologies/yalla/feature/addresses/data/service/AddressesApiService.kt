package uz.ildam.technologies.yalla.feature.addresses.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import uz.ildam.technologies.yalla.core.data.exception.safeApiCall
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.addresses.data.response.AddressRemoteModel
import uz.ildam.technologies.yalla.feature.addresses.data.url.AddressesUrl
import uz.ildam.technologies.yalla.feature.addresses.domain.model.request.PostOneAddressDto

class AddressesApiService(
    private val ktor: HttpClient
) {
    suspend fun findAll(): Result<ApiResponseWrapper<List<AddressRemoteModel>>, DataError.Network> =
        safeApiCall {
            ktor.get(AddressesUrl.FIND_ALL).body()
        }

    suspend fun findOne(id: Int): Result<ApiResponseWrapper<AddressRemoteModel>, DataError.Network> =
        safeApiCall {
            ktor.get(AddressesUrl.FIND_ONE + id).body()
        }

    suspend fun postOne(body: PostOneAddressDto): Result<ApiResponseWrapper<List<String>>, DataError.Network> =
        safeApiCall {
            ktor.post(AddressesUrl.POST_ONE) { setBody(body) }.body()
        }

    suspend fun deleteOne(id: Int): Result<Unit, DataError.Network> = safeApiCall {
        ktor.delete(AddressesUrl.DELETE + id).body()
    }

    suspend fun updateOne(id: Int, body: PostOneAddressDto): Result<Unit, DataError.Network> =
        safeApiCall {
            ktor.put(AddressesUrl.UPDATE + id) { setBody(body) }.body()
        }
}