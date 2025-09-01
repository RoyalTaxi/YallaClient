package uz.yalla.client.service.payment.service

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.yalla.client.service.payment.request.AddCardRequest
import uz.yalla.client.service.payment.request.VerifyCardRequest
import uz.yalla.client.service.payment.response.AddCardResponse
import uz.yalla.client.service.payment.url.PaymentUrl
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall

class AddCardApiService(
    private val ktor: HttpClient
) {
    suspend fun addCard(body: AddCardRequest): Either<ApiResponseWrapper<AddCardResponse>, DataError.Network> =
        safeApiCall {
            ktor.post(PaymentUrl.ADD_CARD) { setBody(body) }
        }

    suspend fun verifyCard(body: VerifyCardRequest): Either<Unit, DataError.Network> = safeApiCall {
        ktor.post(PaymentUrl.VERIFY_CARD) { setBody(body) }
    }
}
