package uz.yalla.client.core.service.network

import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.SerializationException
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either

suspend inline fun <reified T> safeApiCall(
    call: () -> HttpResponse
): Either<T, DataError.Network> {
    return try {
        val response = call()
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
