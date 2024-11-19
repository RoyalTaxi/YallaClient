package uz.ildam.technologies.yalla.core.data.exception

import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.delay
import kotlinx.serialization.SerializationException
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result

suspend inline fun <reified T> safeApiCall(
    retryCount: Int = 3,
    initialDelayMillis: Long = 1000L,
    call: () -> HttpResponse
): Result<T, DataError.Network> {
    var currentRetry = 0
    var currentDelay = initialDelayMillis

    while (true) {
        val result: Result<T, DataError.Network> = try {
            val response = call()
            when (response.status.value) {
                in 200..299 -> Result.Success(response.body())
                in 400..499 -> Result.Error(DataError.Network.CLIENT_REQUEST_ERROR)
                in 300..399 -> Result.Error(DataError.Network.REDIRECT_RESPONSE_ERROR)
                in 500..599 -> Result.Error(DataError.Network.SERVER_RESPONSE_ERROR)
                else -> Result.Error(DataError.Network.UNKNOWN_ERROR)
            }
        } catch (e: ServerResponseException) {
            Result.Error(DataError.Network.SERVER_RESPONSE_ERROR)
        } catch (e: ClientRequestException) {
            Result.Error(DataError.Network.CLIENT_REQUEST_ERROR)
        } catch (e: RedirectResponseException) {
            Result.Error(DataError.Network.REDIRECT_RESPONSE_ERROR)
        } catch (e: IOException) {
            Result.Error(DataError.Network.NO_INTERNET_ERROR)
        } catch (e: SocketTimeoutException) {
            Result.Error(DataError.Network.SOCKET_TIME_OUT_ERROR)
        } catch (e: SerializationException) {
            Result.Error(DataError.Network.SERIALIZATION_ERROR)
        } catch (e: ResponseException) {
            Result.Error(DataError.Network.UNKNOWN_ERROR)
        }

        if (result is Result.Success || currentRetry >= retryCount) return result

        if (result is Result.Error && result.error == DataError.Network.CLIENT_REQUEST_ERROR) {
            delay(currentDelay)
            currentDelay *= 2
            currentRetry++
        } else return result
    }
}
