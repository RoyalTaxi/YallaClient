package uz.yalla.client.service.payment.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import uz.yalla.client.service.payment.response.CardListItemRemoteModel
import uz.yalla.client.service.payment.url.PaymentUrl
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall

class CardListApiService(
    private val ktor: HttpClient
) {
    suspend fun getCardList(): Either<ApiResponseWrapper<List<CardListItemRemoteModel>>, DataError.Network> = safeApiCall {
        ktor.get(PaymentUrl.CARD_LIST).body()
    }
}