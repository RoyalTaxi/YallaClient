package uz.yalla.client.feature.profile.data.repository

import uz.yalla.client.core.data.mapper.ClientMapper
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
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
    override suspend fun getMe(): Either<GetMeModel, DataError.Network> {
        return when (val result = service.getMe()) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(ProfileMapper.mapper))
        }
    }

    override suspend fun updateMe(body: UpdateMeDto): Either<Client, DataError.Network> {
        return when (
            val result = service.updateMe(
                body.let { dto ->
                    UpdateMeRequest(
                        given_names = dto.givenNames,
                        sur_name = dto.surname,
                        birthday = dto.birthday,
                        gender = dto.gender,
                        image = dto.image
                    )
                }
            )
        ) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(ClientMapper.clientMapper))
        }
    }

    override suspend fun updateAvatar(image: ByteArray): Either<UpdateAvatarModel, DataError.Network> {
        return when (val result = service.updateAvatar(image)) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(ProfileMapper.avatarMapper))
        }
    }
}