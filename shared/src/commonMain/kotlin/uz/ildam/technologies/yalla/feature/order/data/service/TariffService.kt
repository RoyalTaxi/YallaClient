package uz.ildam.technologies.yalla.feature.order.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.ildam.technologies.yalla.core.data.exception.safeApiCall
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.data.request.tariff.GetTariffsRequest
import uz.ildam.technologies.yalla.feature.order.data.response.tariff.GetTariffsResponse
import uz.ildam.technologies.yalla.feature.order.data.url.TariffUrl

class TariffService(
    private val ktor: HttpClient
) {
    suspend fun getTariffs(body: GetTariffsRequest): Result<ApiResponseWrapper<GetTariffsResponse>, DataError.Network> =
        safeApiCall {
            ktor.post(TariffUrl.GET_TARIFFS) { setBody(body) }.body()
        }
}