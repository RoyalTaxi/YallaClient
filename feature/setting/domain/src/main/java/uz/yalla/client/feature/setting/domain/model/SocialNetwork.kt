package uz.yalla.client.feature.setting.domain.model

data class SocialNetwork (
    val iconResId: Int,
    val value: String,
    val titleResId: Int,
    val type: SocialNetworkType
)

enum class SocialNetworkType {
    TELEGRAM,
    INSTAGRAM,
    PHONE,
    EMAIL,
    YOUTUBE,
    FACEBOOK
}