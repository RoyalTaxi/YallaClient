package uz.yalla.client.feature.contact.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.feature.contact.R
import uz.yalla.client.feature.setting.domain.model.SocialNetwork
import uz.yalla.client.feature.setting.domain.model.SocialNetworkType
import uz.yalla.client.feature.setting.domain.usecase.GetConfigUseCase

 class ContactUsViewModel(
    private val getConfigUseCase: GetConfigUseCase,
    private val prefs: AppPreferences
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ContactUsUIState())
    val uiState = _uiState.asStateFlow()

    fun getConfig() = viewModelScope.launchWithLoading {
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
            }
            .onFailure(::handleException)
    }
}