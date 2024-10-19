package uz.ildam.technologies.yalla.android.ui.screens.credentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import uz.ildam.technologies.yalla.android.utils.Utils.formatWithDotsDMY
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.register.RegisterUseCase

internal class CredentialsViewModel(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val eventChannel = Channel<CredentialsEvent>()
    val events = eventChannel.receiveAsFlow()

    fun register(
        number: String,
        firstName: String,
        lastName: String,
        gender: String,
        dateOfBirth: LocalDate,
        key: String
    ) = viewModelScope.launch {
        when (
            val result = registerUseCase(
                number, firstName, lastName, gender, dateOfBirth.formatWithDotsDMY(), key
            )
        ) {
            is Result.Error -> eventChannel.send(CredentialsEvent.Error("server error"))

            is Result.Success -> {
                AppPreferences.accessToken = result.data.accessToken
                AppPreferences.tokenType = result.data.accessToken
                AppPreferences.isDeviceRegistered = true
                AppPreferences.number = number
                AppPreferences.firstName = firstName
                AppPreferences.lastName = lastName
                AppPreferences.gender = gender
                AppPreferences.dateOfBirth = dateOfBirth.formatWithDotsDMY()
                eventChannel.send(CredentialsEvent.Success(result.data))
            }
        }
    }
}