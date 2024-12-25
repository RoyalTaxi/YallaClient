package uz.ildam.technologies.yalla.feature.profile.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.ClientMapper
import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.feature.profile.data.response.GetMeResponse
import uz.ildam.technologies.yalla.feature.profile.domain.model.response.GetMeModel

object ProfileMapper {
    val mapper: Mapper<GetMeResponse?, GetMeModel> = { remote ->
        GetMeModel(
            client = remote?.client.let(ClientMapper.clientMapper)
        )
    }
}