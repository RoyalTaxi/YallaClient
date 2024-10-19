package uz.ildam.technologies.yalla.feature.auth.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.ClientMapper
import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.feature.auth.data.response.register.RegisterResponse
import uz.ildam.technologies.yalla.feature.auth.domain.model.register.RegisterModel

object RegisterMapper {
    val mapper: Mapper<RegisterResponse?, RegisterModel> = { remote ->
        RegisterModel(
            tokenType = remote?.token_type.orEmpty(),
            accessToken = remote?.access_token.orEmpty(),
            expiresIn = remote?.expires_in.orEmpty(),
            client = remote?.client?.let(ClientMapper.clientMapper)
        )
    }
}