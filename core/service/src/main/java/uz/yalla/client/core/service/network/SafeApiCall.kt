package uz.yalla.client.core.service.network

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
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either

suspend inline fun <reified T> safeApiCall(
    crossinline call: suspend () -> HttpResponse
): Either<T, DataError.Network> {
    return try {
        val response = retryIO { call() }
        when (response.status.value) {
            in 200..299 -> {
                if (T::class == Unit::class) {
                    Either.Success(Unit as T)
                } else {
                    Either.Success(response.body())
                }
            }

            in 300..399 -> Either.Error(DataError.Network.REDIRECT_RESPONSE_ERROR)
            in 400..499 -> Either.Error(DataError.Network.CLIENT_REQUEST_ERROR)
            in 500..599 -> Either.Error(DataError.Network.SERVER_RESPONSE_ERROR)
            else -> Either.Error(DataError.Network.UNKNOWN_ERROR)
        }
    } catch (e: ServerResponseException) {
        Either.Error(DataError.Network.SERVER_RESPONSE_ERROR)
    } catch (e: ClientRequestException) {
        Either.Error(DataError.Network.CLIENT_REQUEST_ERROR)
    } catch (e: RedirectResponseException) {
        Either.Error(DataError.Network.REDIRECT_RESPONSE_ERROR)
    } catch (e: IOException) {
        Either.Error(DataError.Network.NO_INTERNET_ERROR)
    } catch (e: SocketTimeoutException) {
        Either.Error(DataError.Network.SOCKET_TIME_OUT_ERROR)
    } catch (e: SerializationException) {
        Either.Error(DataError.Network.SERIALIZATION_ERROR)
    } catch (e: ResponseException) {
        Either.Error(DataError.Network.UNKNOWN_ERROR)
    }
}


suspend fun <T> retryIO(
    times: Int = 3,
    initialDelay: Long = 100,
    maxDelay: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelay

    repeat(times - 1) {
        try {
            return block()
        } catch (_: Exception) {
        }

        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }

    return block()
}