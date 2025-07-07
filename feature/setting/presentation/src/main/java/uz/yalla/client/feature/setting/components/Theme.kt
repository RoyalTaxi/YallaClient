package uz.yalla.client.feature.setting.components

import androidx.annotation.StringRes
import uz.yalla.client.core.domain.model.type.ThemeType

data class Theme(
    @StringRes val stringResId: Int,
    val themeType: ThemeType,
)