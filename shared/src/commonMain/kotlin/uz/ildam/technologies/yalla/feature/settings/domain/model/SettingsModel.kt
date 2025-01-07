package uz.ildam.technologies.yalla.feature.settings.domain.model

data class SettingsModel(
    val setting: Setting
) {
    data class Setting(
        val facebook: String,
        val instagram: String,
        val youtube: String,
        val telegram: String,
        val privacyPolicyRu: String,
        val privacyPolicyUz: String
    )
}
