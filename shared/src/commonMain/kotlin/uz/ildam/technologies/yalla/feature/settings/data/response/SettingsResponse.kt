package uz.ildam.technologies.yalla.feature.settings.data.response

import kotlinx.serialization.Serializable

@Serializable
data class SettingsResponse(
    val setting: Setting?
) {
    @Serializable
    data class Setting(
        val facebook: String?,
        val instagram: String?,
        val youtube: String?,
        val telegram_nickname: String?,
        val privacy_policy_ru: String?,
        val privacy_policy_uz: String?
    )
}
