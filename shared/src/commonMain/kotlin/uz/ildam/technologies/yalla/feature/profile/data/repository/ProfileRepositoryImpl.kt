package uz.ildam.technologies.yalla.feature.profile.data.repository

import uz.ildam.technologies.yalla.core.data.mapper.ClientMapper
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.core.domain.model.ClientModel
import uz.ildam.technologies.yalla.feature.profile.data.mapper.ProfileMapper
import uz.ildam.technologies.yalla.feature.profile.data.request.UpdateMeRequest
import uz.ildam.technologies.yalla.feature.profile.data.service.ProfileService
import uz.ildam.technologies.yalla.feature.profile.domain.model.request.UpdateMeDto
import uz.ildam.technologies.yalla.feature.profile.domain.model.response.GetMeModel
import uz.ildam.technologies.yalla.feature.profile.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val service: ProfileService
) : ProfileRepository {
    override suspend fun getMe(): Result<GetMeModel, DataError.Network> {
        return when (val result = service.getMe()) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(result.data.result.let(ProfileMapper.mapper))
        }
    }

    override suspend fun updateMe(body: UpdateMeDto): Result<ClientModel, DataError.Network> {
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
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(result.data.result.let(ClientMapper.clientMapper))
        }
    }
}