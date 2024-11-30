package uz.ildam.technologies.yalla.feature.order.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.ildam.technologies.yalla.core.data.exception.safeApiCall
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.data.request.order.OrderTaxiRequest
import uz.ildam.technologies.yalla.feature.order.data.response.order.OrderTaxiResponse
import uz.ildam.technologies.yalla.feature.order.data.url.OrderUrl.ORDER_TAXI

class OrderService(
    private val ktor: HttpClient
) {
    suspend fun orderTaxi(
        body: OrderTaxiRequest
    ): Result<ApiResponseWrapper<OrderTaxiResponse>, DataError.Network> = safeApiCall {
        ktor.post(ORDER_TAXI) { setBody(body) }.body()
    }
}