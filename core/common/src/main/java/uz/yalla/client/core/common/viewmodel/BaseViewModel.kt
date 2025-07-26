package uz.yalla.client.core.common.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import uz.yalla.client.core.common.R
import uz.yalla.client.core.domain.error.DataError

abstract class BaseViewModel : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _failure = Channel<Int>(Channel.UNLIMITED)
    val failure: Flow<Int> = _failure.receiveAsFlow()

    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog = _showErrorDialog.asStateFlow()

    private val _currentErrorMessageId = MutableStateFlow<Int?>(null)
    val currentErrorMessageId = _currentErrorMessageId.asStateFlow()

    private val handler = CoroutineExceptionHandler { _, e ->
        val messageId = mapThrowableToUserMessage(e)
        _currentErrorMessageId.tryEmit(messageId)
        _showErrorDialog.tryEmit(true)
    }

    protected val viewModelScope = CoroutineScope(
        context = Dispatchers.Main.immediate + SupervisorJob() + handler
    )

    fun handleException(throwable: Throwable) {
        val messageId = mapThrowableToUserMessage(throwable)
        _currentErrorMessageId.tryEmit(messageId)
        _showErrorDialog.tryEmit(true)
    }

    fun dismissErrorDialog() {
        _showErrorDialog.tryEmit(false)
        _currentErrorMessageId.tryEmit(null)
    }

    fun CoroutineScope.launchWithLoading(block: suspend () -> Unit) = launch {
        _loading.emit(true)
        try {
            block()
        } finally {
            _loading.emit(false)
        }
    }

    private fun mapThrowableToUserMessage(throwable: Throwable): Int {
        return when (throwable) {
            DataError.Network.NO_INTERNET_ERROR -> R.string.error_no_internet
            DataError.Network.SOCKET_TIME_OUT_ERROR -> R.string.error_connection_timeout
            DataError.Network.UNAUTHORIZED_ERROR -> R.string.error_session_expired
            DataError.Network.CLIENT_REQUEST_ERROR -> R.string.error_client_request
            DataError.Network.SERVER_RESPONSE_ERROR -> R.string.error_server_busy
            DataError.Network.REDIRECT_RESPONSE_ERROR -> R.string.error_unexpected_redirection
            DataError.Network.SERIALIZATION_ERROR -> R.string.error_data_format
            DataError.Network.UNKNOWN_ERROR -> R.string.error_network_unexpected
            else -> R.string.error_unexpected_fallback
        }
    }

    override fun onCleared() {
        viewModelScope.coroutineContext.cancelChildren()
    }
}