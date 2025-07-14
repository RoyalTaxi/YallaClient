package uz.yalla.client.feature.info.about_app.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.feature.info.R
import uz.yalla.client.feature.setting.domain.model.SocialNetwork
import uz.yalla.client.feature.setting.domain.model.SocialNetworkType
import uz.yalla.client.feature.setting.domain.usecase.GetConfigUseCase

internal class AboutAppViewModel(
    private val getConfigUseCase: GetConfigUseCase,
    private val prefs: AppPreferences
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AboutAppUIState())
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
                        value = result.setting.instagram,
                        type = SocialNetworkType.INSTAGRAM
                    ),
                    SocialNetwork(
                        iconResId = R.drawable.ic_youtube,
                        titleResId = R.string.youtube,
                        value = result.setting.youtube,
                        type = SocialNetworkType.YOUTUBE
                    ),
                    SocialNetwork(
                        iconResId = R.drawable.ic_facebook,
                        titleResId = R.string.facebook,
                        value = result.setting.facebook,
                        type = SocialNetworkType.FACEBOOK
                    )
                )

                val filteredSocialNetworks = allSocialNetworks.filter {
                    it.value.isNotBlank()
                }

                _uiState.update {
                    it.copy(
                        socialNetworks = filteredSocialNetworks,
                        privacyPolicyRu = result.setting.privacyPolicyRu to R.string.user_agreement,
                        privacyPolicyUz = result.setting.privacyPolicyUz to R.string.user_agreement
                    )
                }

                prefs.setSupportNumber(result.setting.supportPhone)
            }
            .onFailure(::handleException)
    }
}