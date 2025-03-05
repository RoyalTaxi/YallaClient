package uz.yalla.client.feature.auth.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.ClientMapper
import uz.yalla.client.feature.auth.domain.model.register.RegisterModel
import uz.yalla.client.service.auth.response.register.RegisterResponse

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