package uz.yalla.client.core.domain.error

sealed class DataError : Error() {
    sealed class Network : DataError() {
        data object UNAUTHORIZED_ERROR : Network()
        data object REDIRECT_RESPONSE_ERROR : Network()
        data object CLIENT_REQUEST_ERROR : Network()
        data object SERVER_RESPONSE_ERROR : Network()
        data object NO_INTERNET_ERROR : Network()
        data object SERIALIZATION_ERROR : Network()
        data object SOCKET_TIME_OUT_ERROR : Network()
        data object UNKNOWN_ERROR : Network()
    }
}