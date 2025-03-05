package uz.yalla.client.service.setting.response

import kotlinx.serialization.Serializable

@Serializable
data class SettingsResponse(
    val setting: Setting?
) {
    @Serializable
    data class Setting(
        val executor_link: String?,
        val invite_link_for_friend: String?,
        val support_email: String?,
        val support_telegram_nickname: String?,
        val support_instagram_nickname: String?,
        val support_phone: String?,
        val facebook: String?,
        val instagram: String?,
        val youtube: String?,
        val telegram_nickname: String?,
        val privacy_policy_ru: String?,
        val privacy_policy_uz: String?
    )
}
