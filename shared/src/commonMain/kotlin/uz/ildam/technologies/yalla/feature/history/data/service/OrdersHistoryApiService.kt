package uz.ildam.technologies.yalla.feature.history.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import uz.ildam.technologies.yalla.core.data.exception.safeApiCall
import uz.ildam.technologies.yalla.core.data.response.ApiPaginationWrapper
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.history.data.response.OrderHistoryResponse
import uz.ildam.technologies.yalla.feature.history.data.response.OrdersHistoryResponseItem
import uz.ildam.technologies.yalla.feature.history.data.url.OrdersHistoryUrl

class OrdersHistoryApiService(private val ktor: HttpClient) {
    suspend fun getOrders(
        page: Int, limit: Int
    ): Result<ApiResponseWrapper<ApiPaginationWrapper<OrdersHistoryResponseItem>>, DataError.Network> =
        safeApiCall {
            ktor.get(OrdersHistoryUrl.ORDERS_ARCHIVE) {
                parameter("page", page)
                parameter("per_page", limit)
            }.body()
        }

    suspend fun getOrder(orderId: Int): Result<ApiResponseWrapper<OrderHistoryResponse>, DataError.Network> =
        safeApiCall {
            ktor.get(OrdersHistoryUrl.GET_ORDER + orderId).body()
        }
}