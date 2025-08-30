package uz.yalla.client.feature.contact.intent

import uz.yalla.client.feature.setting.domain.model.SocialNetwork

data class ContactUsState(
    val socialNetworks: List<SocialNetwork>
) {
    companion object {
        val INITIAL = ContactUsState(
            socialNetworks = emptyList()
        )
    }
}