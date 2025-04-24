package uz.yalla.client.service.notification.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiPaginationWrapper
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall
import uz.yalla.client.service.notification.response.NotificationResponse
import uz.yalla.client.service.notification.url.NotificationsUrl

private const val PARAMETER_PAGE = "page"
private const val PARAMETER_PER_PAGE = "per_page"

class NotificationsApiService(private val ktor: HttpClient) {
    suspend fun getNotifications(
        page: Int, limit: Int
    ): Either<ApiResponseWrapper<ApiPaginationWrapper<NotificationResponse>>, DataError.Network> =
        safeApiCall {
            ktor.get(NotificationsUrl.FIND_ALL) {
                parameter(PARAMETER_PAGE, page)
                parameter(PARAMETER_PER_PAGE, limit)
            }.body()
        }

    suspend fun getNotification(id: Int): Either<ApiResponseWrapper<NotificationResponse>, DataError.Network> =
        safeApiCall {
            ktor.get(NotificationsUrl.FIND_ONE + id).body()
        }

    suspend fun getNotificationCount(): Either<ApiResponseWrapper<Int>, DataError.Network> =
        safeApiCall {
            ktor.get(NotificationsUrl.GET_COUNT).body()
        }
}