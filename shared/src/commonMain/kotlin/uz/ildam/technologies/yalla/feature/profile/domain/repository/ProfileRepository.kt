package uz.ildam.technologies.yalla.feature.profile.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.core.domain.model.ClientModel
import uz.ildam.technologies.yalla.feature.profile.domain.model.request.UpdateMeDto
import uz.ildam.technologies.yalla.feature.profile.domain.model.response.GetMeModel
import uz.ildam.technologies.yalla.feature.profile.domain.model.response.UpdateAvatarModel

interface ProfileRepository {
    suspend fun getMe(): Result<GetMeModel, DataError.Network>
    suspend fun updateMe(body: UpdateMeDto): Result<ClientModel, DataError.Network>
    suspend fun updateAvatar(image: ByteArray): Result<UpdateAvatarModel, DataError.Network>
}
