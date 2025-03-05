package uz.yalla.client.service.order.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.yalla.client.service.order.request.tariff.GetTariffsRequest
import uz.yalla.client.service.order.request.tariff.GetTimeOutRequest
import uz.yalla.client.service.order.response.tariff.GetTariffsResponse
import uz.yalla.client.service.order.response.tariff.GetTimeOutResponse
import uz.yalla.client.service.order.url.TariffUrl
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall

class TariffApiService(
    private val ktor: HttpClient
) {
    suspend fun getTariffs(
        body: GetTariffsRequest
    ): Either<ApiResponseWrapper<GetTariffsResponse>, DataError.Network> = safeApiCall {
        ktor.post(TariffUrl.GET_TARIFFS) { setBody(body) }.body()
    }

    suspend fun getTimeOut(
        body: GetTimeOutRequest
    ): Either<ApiResponseWrapper<GetTimeOutResponse>, DataError.Network> = safeApiCall {
        ktor.get(TariffUrl.GET_TIMEOUT) {
            parameter("lat", body.lat)
            parameter("lng", body.lng)
            parameter("tariff_id", body.tariff_id)
        }.body()
    }
}