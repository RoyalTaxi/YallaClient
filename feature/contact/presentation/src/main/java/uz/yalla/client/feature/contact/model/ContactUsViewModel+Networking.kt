package uz.yalla.client.feature.contact.model

import androidx.lifecycle.viewModelScope
import uz.yalla.client.feature.contact.R
import uz.yalla.client.feature.setting.domain.model.SocialNetwork
import uz.yalla.client.feature.setting.domain.model.SocialNetworkType

fun ContactUsViewModel.getConfig() = intent {
    viewModelScope.launchWithLoading {
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

                reduce {
                    state.copy(socialNetworks = filteredSocialNetworks)
                }

                prefs.setSupportNumber(result.setting.supportPhone)
            }
            .onFailure(::handleException)
    }
}