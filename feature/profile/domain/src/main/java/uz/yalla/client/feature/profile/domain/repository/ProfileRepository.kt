package uz.yalla.client.feature.profile.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.domain.model.Client
import uz.yalla.client.feature.profile.domain.model.request.UpdateMeDto
import uz.yalla.client.feature.profile.domain.model.response.GetMeModel
import uz.yalla.client.feature.profile.domain.model.response.UpdateAvatarModel

interface ProfileRepository {
    suspend fun getMe(): Either<GetMeModel, DataError.Network>
    suspend fun updateMe(body: UpdateMeDto): Either<Client, DataError.Network>
    suspend fun updateAvatar(image: ByteArray): Either<UpdateAvatarModel, DataError.Network>
}
