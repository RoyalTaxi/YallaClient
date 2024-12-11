package uz.ildam.technologies.yalla.feature.payment.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import uz.ildam.technologies.yalla.core.data.exception.safeApiCall
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.payment.data.response.CardListItemRemoteModel
import uz.ildam.technologies.yalla.feature.payment.data.url.PaymentUrl

class CardListApiService(
    private val ktor: HttpClient
) {
    suspend fun getCardList(): Result<List<CardListItemRemoteModel>, DataError.Network> = safeApiCall {
        ktor.get(PaymentUrl.CARD_LIST).body()
    }
}