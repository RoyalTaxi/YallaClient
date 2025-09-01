package uz.yalla.client.core.data.ext

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper

fun <T> Either<ApiResponseWrapper<T>, DataError.Network>.mapResult(): Either<T?, DataError.Network> =
    when (this) {
        is Either.Error -> Either.Error(this.error)
        is Either.Success -> Either.Success(this.data.result)
    }

inline fun <T, R> Either<ApiResponseWrapper<T>, DataError.Network>.mapResult(
    crossinline mapper: (T?) -> R
): Either<R, DataError.Network> = when (val unwrapped = this.mapResult()) {
    is Either.Error -> Either.Error(unwrapped.error)
    is Either.Success -> Either.Success(mapper(unwrapped.data))
}

inline fun <T, R> Either<T, DataError.Network>.mapSuccess(
    crossinline mapper: (T) -> R
): Either<R, DataError.Network> = when (this) {
    is Either.Error -> Either.Error(this.error)
    is Either.Success -> Either.Success(mapper(this.data))
}
