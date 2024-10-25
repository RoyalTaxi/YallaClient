package uz.ildam.technologies.yalla.core.domain.error

sealed interface DataError : Error {
    enum class Network : DataError {
        REDIRECT_RESPONSE_EXCEPTION,
        CLIENT_REQUEST_ERROR,
        SERVER_RESPONSE_ERROR,
        NO_INTERNET_ERROR,
        SERIALIZATION_ERROR,
        SOCKET_TIME_OUT_ERROR,
        UNKNOWN
    }
}