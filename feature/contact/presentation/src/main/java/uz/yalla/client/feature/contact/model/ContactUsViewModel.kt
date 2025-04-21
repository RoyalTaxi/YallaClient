package uz.yalla.client.feature.contact.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.feature.contact.R
import uz.yalla.client.feature.setting.domain.usecase.GetConfigUseCase

internal class ContactUsViewModel(
    private val getConfigUseCase: GetConfigUseCase,
    private val prefs: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactUsUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<ContactUsActionState>()
    val actionState = _actionState.asSharedFlow()

    fun getConfig() = viewModelScope.launch(Dispatchers.IO) {
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
                                result.setting.supportInstagramNickname,
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
                prefs.setSupportNumber(result.setting.supportPhone)
                _actionState.emit(ContactUsActionState.Success)
            }
            .onFailure {
                _actionState.emit(ContactUsActionState.Error)
            }
    }
}