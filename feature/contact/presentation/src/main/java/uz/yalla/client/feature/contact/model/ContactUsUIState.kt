package uz.yalla.client.feature.contact.model

import uz.yalla.client.feature.setting.domain.model.SocialNetwork

 data class ContactUsUIState (
    val socialNetworks: List<SocialNetwork> = emptyList()
)