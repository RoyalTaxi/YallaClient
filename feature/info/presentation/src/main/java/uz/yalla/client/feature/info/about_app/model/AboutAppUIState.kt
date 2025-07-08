package uz.yalla.client.feature.info.about_app.model

import uz.yalla.client.feature.setting.domain.model.SocialNetwork


internal data class AboutAppUIState(
    val privacyPolicyRu: Pair<String, Int>? = null,
    val privacyPolicyUz: Pair<String, Int>? = null,
    val socialNetworks: List<SocialNetwork> = emptyList()
)