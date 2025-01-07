package uz.ildam.technologies.yalla.feature.settings.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.settings.domain.model.SettingsModel

interface SettingRepository {
    suspend fun getConfig(): Either<SettingsModel, DataError.Network>
}