package uz.yalla.client.feature.setting.domain.model

data class SettingsModel(
    val setting: Setting
) {
    data class Setting(
        val executorLink: String,
        val inviteLinkForFriend: String,
        val supportEmail: String,
        val supportTelegramNickname: String,
        val supportInstagramNickname: String,
        val supportPhone: String,
        val facebook: String,
        val instagram: String,
        val youtube: String,
        val telegram: String,
        val privacyPolicyRu: String,
        val privacyPolicyUz: String
    )
}
