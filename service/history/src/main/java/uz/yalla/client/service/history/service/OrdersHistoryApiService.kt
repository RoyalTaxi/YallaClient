package uz.yalla.client.service.history.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import uz.yalla.client.service.history.url.OrdersHistoryUrl
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiPaginationWrapper
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall
import uz.yalla.client.service.history.response.OrderHistoryResponse
import uz.yalla.client.service.history.response.OrdersHistoryResponseItem

private const val PARAMETER_PAGE = "page"
private const val PARAMETER_PER_PAGE = "per_page"

class OrdersHistoryApiService(private val ktor: HttpClient) {
    suspend fun getOrders(
        page: Int, limit: Int
    ): Either<ApiResponseWrapper<ApiPaginationWrapper<OrdersHistoryResponseItem>>, DataError.Network> =
        safeApiCall(isIdempotent = true) {
            ktor.get(OrdersHistoryUrl.ORDERS_ARCHIVE) {
                parameter(PARAMETER_PAGE, page)
                parameter(PARAMETER_PER_PAGE, limit)
            }
        }

    suspend fun getOrder(orderId: Int): Either<ApiResponseWrapper<OrderHistoryResponse>, DataError.Network> =
        safeApiCall(isIdempotent = true) {
            ktor.get(OrdersHistoryUrl.GET_ORDER) {
                url { appendPathSegments(orderId.toString()) }
            }
        }
}
