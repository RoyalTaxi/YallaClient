package uz.yalla.client.service.order.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import uz.yalla.client.service.order.request.order.CancelOrderReason
import uz.yalla.client.service.order.request.order.OrderTaxiRequest
import uz.yalla.client.service.order.request.order.RateTheRideRequest
import uz.yalla.client.service.order.response.order.ActiveOrdersResponse
import uz.yalla.client.service.order.response.order.OrderTaxiResponse
import uz.yalla.client.service.order.response.order.SearchCarResponse
import uz.yalla.client.service.order.response.order.SettingResponse
import uz.yalla.client.service.order.response.order.ShowOrderResponse
import uz.yalla.client.service.order.url.OrderUrl
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall

class OrderApiService(
    private val ktorApi1: HttpClient,
    private val ktorApi2: HttpClient
) {
    suspend fun orderTaxi(
        body: OrderTaxiRequest
    ): Either<ApiResponseWrapper<OrderTaxiResponse>, DataError.Network> = safeApiCall {
        ktorApi2.post(OrderUrl.ORDER_TAXI) { setBody(body) }.body()
    }

    suspend fun searchCars(
        lat: Double,
        lng: Double,
        tariffId: Int
    ): Either<ApiResponseWrapper<SearchCarResponse>, DataError.Network> = safeApiCall {
        ktorApi2.get(OrderUrl.SEARCH_CARS) {
            parameter("lat", lat)
            parameter("lng", lng)
            parameter("tariff_id", tariffId)
        }.body()
    }

    suspend fun getSetting(): Either<ApiResponseWrapper<SettingResponse>, DataError.Network> =
        safeApiCall {
            ktorApi2.get(OrderUrl.GET_SETTING).body()
        }

    suspend fun cancelRide(orderId: Int): Either<Unit, DataError.Network> = safeApiCall {
        ktorApi2.put(OrderUrl.CANCEL_RIDE + orderId)
    }

    suspend fun cancelReason(
        orderId: Int,
        reasonId: Int,
        reasonComment: String
    ): Either<Unit, DataError.Network> = safeApiCall {
        ktorApi2.put(OrderUrl.CANCEL_REASON + orderId) {
            setBody(
                CancelOrderReason(
                    reason_id = reasonId,
                    reason_comment = reasonComment
                )
            )
        }
    }

    suspend fun show(orderId: Int): Either<ApiResponseWrapper<ShowOrderResponse>, DataError.Network> =
        safeApiCall {
            ktorApi2.get(OrderUrl.SHOW + orderId).body()
        }

    suspend fun rateTheRide(
        ball: Int,
        orderId: Int,
        comment: String
    ): Either<Unit, DataError.Network> = safeApiCall {
        ktorApi1.post(OrderUrl.RATINGS) {
            setBody(
                RateTheRideRequest(
                    order_id = orderId,
                    ball = ball,
                    comment = comment
                )
            )
        }.body()
    }

    suspend fun getActiveOrders(): Either<ApiResponseWrapper<ActiveOrdersResponse>, DataError.Network> =
        safeApiCall { ktorApi2.get(OrderUrl.ACTIVE_ORDERS).body() }
}