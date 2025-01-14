package uz.ildam.technologies.yalla.feature.profile.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.core.domain.model.Client
import uz.ildam.technologies.yalla.feature.profile.domain.model.request.UpdateMeDto
import uz.ildam.technologies.yalla.feature.profile.domain.model.response.GetMeModel
import uz.ildam.technologies.yalla.feature.profile.domain.model.response.UpdateAvatarModel

interface ProfileRepository {
    suspend fun getMe(): Either<GetMeModel, DataError.Network>
    suspend fun updateMe(body: UpdateMeDto): Either<Client, DataError.Network>
    suspend fun updateAvatar(image: ByteArray): Either<UpdateAvatarModel, DataError.Network>
}
