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
import uz.yalla.client.feature.setting.domain.model.SocialNetwork
import uz.yalla.client.feature.setting.domain.model.SocialNetworkType
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
                val allSocialNetworks = listOf(
                    SocialNetwork(
                        iconResId = R.drawable.ic_telegram,
                        titleResId = R.string.telegram,
                        value = result.setting.supportTelegramNickname,
                        type = SocialNetworkType.TELEGRAM
                    ),
                    SocialNetwork(
                        iconResId = R.drawable.ic_instagram,
                        titleResId = R.string.instagram,
                        value = result.setting.supportInstagramNickname,
                        type = SocialNetworkType.INSTAGRAM
                    ),
                    SocialNetwork(
                        iconResId = R.drawable.ic_call,
                        titleResId = R.string.contuct_us,
                        value = result.setting.supportPhone,
                        type = SocialNetworkType.PHONE
                    ),
                    SocialNetwork(
                        iconResId = R.drawable.ic_email,
                        titleResId = R.string.email,
                        value = result.setting.supportEmail,
                        type = SocialNetworkType.EMAIL
                    )
                )

                val filteredSocialNetworks = allSocialNetworks.filter {
                    it.value.isNotBlank()
                }

                _uiState.update {
                    it.copy(socialNetworks = filteredSocialNetworks)
                }

                prefs.setSupportNumber(result.setting.supportPhone)
                _actionState.emit(ContactUsActionState.Success)
            }
            .onFailure {
                _actionState.emit(ContactUsActionState.Error)
            }
    }
}