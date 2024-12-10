package uz.ildam.technologies.yalla.feature.payment.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.ildam.technologies.yalla.core.data.exception.safeApiCall
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.payment.data.request.AddCardRequest
import uz.ildam.technologies.yalla.feature.payment.data.request.VerifyCardRequest
import uz.ildam.technologies.yalla.feature.payment.data.response.AddCardResponse
import uz.ildam.technologies.yalla.feature.payment.data.url.PaymentUrl

class AddCardApiService(
    private val ktor: HttpClient
) {
    suspend fun addCard(body: AddCardRequest): Result<ApiResponseWrapper<AddCardResponse>, DataError.Network> =
        safeApiCall {
            ktor.post(PaymentUrl.ADD_CARD) { setBody(body) }.body()
        }

    suspend fun verifyCard(body: VerifyCardRequest): Result<Unit, DataError.Network> = safeApiCall {
        ktor.post(PaymentUrl.VERIFY_CARD) { setBody(body) }
    }
}