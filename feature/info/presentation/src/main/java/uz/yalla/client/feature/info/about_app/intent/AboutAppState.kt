package uz.yalla.client.feature.info.about_app.intent

import uz.yalla.client.feature.setting.domain.model.SocialNetwork


data class AboutAppState(
    val privacyPolicyRu: Pair<String, Int>?,
    val privacyPolicyUz: Pair<String, Int>?,
    val socialNetworks: List<SocialNetwork>
) {
    companion object {
        val INITIAL = AboutAppState(
            privacyPolicyRu = null,
            privacyPolicyUz = null,
            socialNetworks = emptyList()
        )
    }
}