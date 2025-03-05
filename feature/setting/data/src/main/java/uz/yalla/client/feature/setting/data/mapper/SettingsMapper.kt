package uz.yalla.client.feature.setting.data.mapper


import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.feature.setting.domain.model.SettingsModel
import uz.yalla.client.service.setting.response.SettingsResponse

object SettingsMapper {
    val mapper: Mapper<SettingsResponse?, SettingsModel> = { remote ->
        SettingsModel(
            setting = remote?.setting.let(settingMapper)
        )
    }

    private val settingMapper: Mapper<SettingsResponse.Setting?, SettingsModel.Setting> =
        { remote ->
            SettingsModel.Setting(
                facebook = remote?.facebook.orEmpty(),
                instagram = remote?.instagram.orEmpty(),
                youtube = remote?.youtube.orEmpty(),
                telegram = remote?.telegram_nickname.orEmpty(),
                privacyPolicyRu = remote?.privacy_policy_ru.orEmpty(),
                privacyPolicyUz = remote?.privacy_policy_uz.orEmpty(),
                supportTelegramNickname = remote?.support_telegram_nickname.orEmpty(),
                supportEmail = remote?.support_email.orEmpty(),
                inviteLinkForFriend = remote?.invite_link_for_friend.orEmpty(),
                executorLink = remote?.executor_link.orEmpty(),
                supportPhone = remote?.support_phone.orEmpty(),
                supportInstagramNickname = remote?.support_instagram_nickname.orEmpty(),
            )
        }
}