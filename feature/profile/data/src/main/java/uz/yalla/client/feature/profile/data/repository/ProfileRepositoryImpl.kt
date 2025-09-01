package uz.yalla.client.feature.profile.data.repository

import uz.yalla.client.core.data.mapper.ClientMapper
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.data.ext.mapResult
import uz.yalla.client.core.data.ext.mapSuccess
import uz.yalla.client.core.domain.model.Client
import uz.yalla.client.feature.profile.data.mapper.ProfileMapper
import uz.yalla.client.feature.profile.domain.model.request.UpdateMeDto
import uz.yalla.client.feature.profile.domain.model.response.GetMeModel
import uz.yalla.client.feature.profile.domain.model.response.UpdateAvatarModel
import uz.yalla.client.feature.profile.domain.repository.ProfileRepository
import uz.yalla.client.service.profile.request.UpdateMeRequest
import uz.yalla.client.service.profile.service.ProfileService

class ProfileRepositoryImpl(
    private val service: ProfileService
) : ProfileRepository {
    override suspend fun getMe(): Either<GetMeModel, DataError.Network> =
        service.getMe().mapResult(ProfileMapper.mapper)

    override suspend fun updateMe(body: UpdateMeDto): Either<Client, DataError.Network> =
        service.updateMe(
            body.let { dto ->
                UpdateMeRequest(
                    given_names = dto.givenNames,
                    sur_name = dto.surname,
                    birthday = dto.birthday,
                    gender = dto.gender,
                    image = dto.image
                )
            }
        ).mapResult(ClientMapper.clientMapper)

    override suspend fun updateAvatar(image: ByteArray): Either<UpdateAvatarModel, DataError.Network> =
        service.updateAvatar(image).mapResult(ProfileMapper.avatarMapper)
}
