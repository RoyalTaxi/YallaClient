package uz.yalla.client.feature.android.contact.contact_us.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.feature.settings.domain.usecase.GetConfigUseCase
import uz.yalla.client.feature.android.contact.R

internal class ContactUsViewModel(
    private val getConfigUseCase: GetConfigUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactUsUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<ContactUsActionState>()
    val actionState = _actionState.asSharedFlow()

    fun getConfig() = viewModelScope.launch {
        _actionState.emit(ContactUsActionState.Loading)
        getConfigUseCase()
            .onSuccess { result ->
                _uiState.update {
                    it.copy(
                        socialNetworks = listOf(
                            Triple(
                                R.drawable.ic_telegram,
                                result.setting.supportTelegramNickname,
                                R.string.telegram
                            ),
                            Triple(
                                R.drawable.ic_instagram,
                                result.setting.supportTelegramNickname,
                                R.string.instagram
                            ),
                            Triple(
                                R.drawable.ic_call,
                                result.setting.supportPhone,
                                R.string.contuct_us
                            ),
                            Triple(
                                R.drawable.ic_email,
                                result.setting.supportEmail,
                                R.string.email
                            )
                        )
                    )
                }
                AppPreferences.supportNumber = result.setting.supportPhone
                _actionState.emit(ContactUsActionState.Success)
            }
            .onFailure {
                _actionState.emit(ContactUsActionState.Error)
            }
    }
}
