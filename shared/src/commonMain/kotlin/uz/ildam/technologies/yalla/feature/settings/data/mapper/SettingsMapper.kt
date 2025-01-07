package uz.ildam.technologies.yalla.feature.settings.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.feature.settings.data.response.SettingsResponse
import uz.ildam.technologies.yalla.feature.settings.domain.model.SettingsModel

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