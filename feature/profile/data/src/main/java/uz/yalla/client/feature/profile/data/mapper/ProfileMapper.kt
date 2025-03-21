package uz.yalla.client.feature.profile.data.mapper

import uz.yalla.client.core.data.mapper.ClientMapper
import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.feature.profile.domain.model.response.GetMeModel
import uz.yalla.client.feature.profile.domain.model.response.UpdateAvatarModel
import uz.yalla.client.service.profile.response.GetMeResponse
import uz.yalla.client.service.profile.response.UpdateAvatarResponse

object ProfileMapper {
    val mapper: Mapper<GetMeResponse?, GetMeModel> = { remote ->
        GetMeModel(
            client = remote?.client.let(ClientMapper.clientMapper)
        )
    }

    val avatarMapper: Mapper<UpdateAvatarResponse?, UpdateAvatarModel> = { remote ->
        UpdateAvatarModel(
            image = remote?.image.orEmpty()
        )
    }
}