package uz.yalla.client.service.payment.service

import io.ktor.client.HttpClient
import io.ktor.client.request.put
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.network.safeApiCall
import uz.yalla.client.service.payment.url.PaymentUrl

class DeleteCardApiService(
    private val ktor: HttpClient
) {
    suspend fun deleteCard(cardId: String) : Either<Unit, DataError.Network> = safeApiCall {
        ktor.put("${PaymentUrl.DELETE_CARD}$cardId")
    }
}