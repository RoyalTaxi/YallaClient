package uz.ildam.technologies.yalla.android.ui.screens.credentials

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import uz.ildam.technologies.yalla.android.utils.Utils.formatWithDotsDMY
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.core.domain.model.Result
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.register.RegisterUseCase

class CredentialsModel(
    private val registerUseCase: RegisterUseCase,
) : ScreenModel {

    private val eventChannel = Channel<CredentialsEvent>()
    val events = eventChannel.receiveAsFlow()

    fun register(
        number: String,
        firstName: String,
        lastName: String,
        gender: String,
        dateOfBirth: LocalDate,
        key: String
    ) =
        screenModelScope.launch {
            when (
                val result = registerUseCase(
                    number, firstName, lastName, gender, dateOfBirth.formatWithDotsDMY(), key
                )
            ) {
                is Result.Error -> eventChannel.send(CredentialsEvent.Error("server error"))

                is Result.Success -> {
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