package uz.yalla.client.feature.auth.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.data.mapper.orFalse
import uz.yalla.client.service.auth.response.auth.ValidateAuthCodeResponse
import uz.yalla.client.core.data.mapper.ClientMapper
import uz.yalla.client.feature.auth.domain.model.auth.SendAuthCodeModel
import uz.yalla.client.feature.auth.domain.model.auth.VerifyAuthCodeModel
import uz.yalla.client.service.auth.response.auth.SendAuthCodeResponse

object AuthMapper {
    val sendAuthCodeMapper: Mapper<SendAuthCodeResponse?, SendAuthCodeModel> = { remote ->
        SendAuthCodeModel(
            time = remote?.time.or0(),
            resultMessage = remote?.result_message.orEmpty()
        )
    }

    val validateAuthCodeMapper: Mapper<ValidateAuthCodeResponse?, VerifyAuthCodeModel> =
        { remote ->
            VerifyAuthCodeModel(
                isClient = remote?.is_client.orFalse(),
                tokenType = remote?.token_type.orEmpty(),
                accessToken = remote?.access_token.orEmpty(),
                expiresIn = remote?.expires_in.or0(),
                client = remote?.client?.let(ClientMapper.clientMapper),
                key = remote?.key.orEmpty()
            )
        }
}