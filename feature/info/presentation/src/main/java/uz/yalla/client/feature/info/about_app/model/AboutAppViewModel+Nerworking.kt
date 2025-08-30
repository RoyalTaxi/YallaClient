package uz.yalla.client.feature.info.about_app.model

import androidx.lifecycle.viewModelScope
import uz.yalla.client.feature.info.R
import uz.yalla.client.feature.setting.domain.model.SocialNetwork
import uz.yalla.client.feature.setting.domain.model.SocialNetworkType

fun AboutAppViewModel.getConfig() = intent {
    viewModelScope.launchWithLoading {
        getConfigUseCase().onSuccess { result ->
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

            intent {
                reduce {
                    state.copy(
                        socialNetworks = filteredSocialNetworks,
                        privacyPolicyRu = result.setting.privacyPolicyRu to R.string.user_agreement,
                        privacyPolicyUz = result.setting.privacyPolicyUz to R.string.user_agreement
                    )
                }
            }

            prefs.setSupportNumber(result.setting.supportPhone)
        }.onFailure(::handleException)
    }
}