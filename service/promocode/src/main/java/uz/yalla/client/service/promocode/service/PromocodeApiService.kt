package uz.yalla.client.service.promocode.service

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall
import uz.yalla.client.service.promocode.request.PromocodeRequest
import uz.yalla.client.service.promocode.response.PromocodeActivationResponse
import uz.yalla.client.service.promocode.url.PromocodeUrl

class PromocodeApiService(
    private val ktorApi1: HttpClient
) {
    suspend fun activatePromocode(
        body: PromocodeRequest
    ): Either<ApiResponseWrapper<PromocodeActivationResponse>, DataError.Network> = safeApiCall {
        ktorApi1.post(PromocodeUrl.ACTIVATE_PROMOCODE) { setBody(body) }
    }
}
