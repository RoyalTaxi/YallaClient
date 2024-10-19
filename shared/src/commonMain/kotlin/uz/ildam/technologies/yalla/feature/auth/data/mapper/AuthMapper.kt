package uz.ildam.technologies.yalla.feature.auth.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.ClientMapper
import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.core.data.mapper.orFalse
import uz.ildam.technologies.yalla.feature.auth.data.response.auth.SendAuthCodeResponse
import uz.ildam.technologies.yalla.feature.auth.data.response.auth.ValidateAuthCodeResponse
import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.SendAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.VerifyAuthCodeModel

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