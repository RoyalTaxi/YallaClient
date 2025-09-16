package uz.yalla.client.service.payment.service

import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.http.appendPathSegments
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.network.safeApiCall
import uz.yalla.client.service.payment.url.PaymentUrl

class DeleteCardApiService(
    private val ktorApi2: HttpClient
) {
    suspend fun deleteCard(cardId: String) : Either<Unit, DataError.Network> = safeApiCall {
        ktorApi2.put(PaymentUrl.DELETE_CARD) {
            url { appendPathSegments(cardId) }
        }
    }
}
