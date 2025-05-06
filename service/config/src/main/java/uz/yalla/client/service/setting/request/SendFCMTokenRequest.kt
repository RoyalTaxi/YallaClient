package uz.yalla.client.service.setting.request

import kotlinx.serialization.Serializable

@Serializable
data class SendFCMTokenRequest(
    val fcm_token: String
)