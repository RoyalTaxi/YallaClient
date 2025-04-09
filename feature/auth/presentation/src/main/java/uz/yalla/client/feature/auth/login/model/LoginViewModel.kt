package uz.yalla.client.feature.auth.login.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.yalla.client.feature.auth.domain.usecase.auth.SendCodeUseCase

internal class LoginViewModel(
    private val sendCodeUseCase: SendCodeUseCase,
) : ViewModel() {

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _navigationChannel: Channel<Int> = Channel(Channel.CONFLATED)
    val navigationChannel = _navigationChannel.receiveAsFlow()

    val sendCodeButtonState = combine(phoneNumber, loading) { number, loading ->
        number.length == 9 && loading.not()
    }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = false
        )

    fun setNumber(number: String) = viewModelScope.launch {
        if (number.length <= 9) _phoneNumber.emit(number)
    }

    fun sendAuthCode(hash: String?) = viewModelScope.launch(Dispatchers.IO) {
        _loading.emit(true)
        sendCodeUseCase(
            number = phoneNumber.value,
            hash = hash
        ).onSuccess { result ->
            _navigationChannel.send(result.time)
        }.onFailure {
            _loading.emit(false)
        }
    }
}
