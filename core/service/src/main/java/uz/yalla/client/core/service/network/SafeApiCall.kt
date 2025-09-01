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
import kotlin.random.Random
import kotlinx.serialization.SerializationException
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either

suspend inline fun <reified T> safeApiCall(
    isIdempotent: Boolean = false,
    crossinline call: suspend () -> HttpResponse
): Either<T, DataError.Network> {
    return try {
        val response = retryIO(isIdempotent = isIdempotent) { call() }
        when (response.status.value) {
            401 -> Either.Error(DataError.Network.UNAUTHORIZED_ERROR)

            302 -> {
                Either.Error(DataError.Network.NOT_SUFFICIENT_BALANCE)
            }

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
    initialDelay: Long = 200,
    maxDelay: Long = 2_000,
    factor: Double = 2.0,
    isIdempotent: Boolean,
    block: suspend () -> T
): T {
    var currentDelay = initialDelay

    repeat(times - 1) {
        try {
            return block()
        } catch (e: Exception) {
            val retryable = isIdempotent && (e is IOException || e is SocketTimeoutException)
            if (!retryable) throw e
        }

        val jitter = Random.nextLong(0, (currentDelay / 2) + 1)
        delay(currentDelay + jitter)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }

    return block()
}
