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
                privacyPolicyUz = remote?.privacy_policy_uz.orEmpty()
            )
        }
}