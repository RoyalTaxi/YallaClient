package uz.ildam.technologies.yalla.feature.order.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.ildam.technologies.yalla.core.data.exception.safeApiCall
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.order.data.request.tariff.GetTariffsRequest
import uz.ildam.technologies.yalla.feature.order.data.request.tariff.GetTimeOutRequest
import uz.ildam.technologies.yalla.feature.order.data.response.tariff.GetTariffsResponse
import uz.ildam.technologies.yalla.feature.order.data.response.tariff.GetTimeOutResponse
import uz.ildam.technologies.yalla.feature.order.data.url.TariffUrl

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