package uz.ildam.technologies.yalla.feature.auth.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.core.data.mapper.orFalse
import uz.ildam.technologies.yalla.feature.auth.data.response.SendAuthCodeResponse
import uz.ildam.technologies.yalla.feature.auth.data.response.ValidateAuthCodeResponse
import uz.ildam.technologies.yalla.feature.auth.domain.model.SendAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.model.ValidateAuthCodeModel

object AuthMapper {
    val sendAuthCodeMapper: Mapper<SendAuthCodeResponse?, SendAuthCodeModel> = { remote ->
        SendAuthCodeModel(
            time = remote?.time.or0(),
            resultMessage = remote?.result_message.orEmpty()
        )
    }

    val validateAuthCodeMapper: Mapper<ValidateAuthCodeResponse?, ValidateAuthCodeModel> =
        { remote ->
            val brandMapper: Mapper<ValidateAuthCodeResponse.Client.Brand?, ValidateAuthCodeModel.Client.Brand> =
                { brand ->
                    ValidateAuthCodeModel.Client.Brand(
                        id = brand?.id.or0(),
                        name = brand?.name.orEmpty()
                    )
                }

            val clientMapper: Mapper<ValidateAuthCodeResponse.Client?, ValidateAuthCodeModel.Client> =
                { client ->
                    ValidateAuthCodeModel.Client(
                        id = client?.id.or0(),
                        phone = client?.phone.orEmpty(),
                        givenNames = client?.given_names.orEmpty(),
                        fatherName = client?.father_name.orEmpty(),
                        surname = client?.sur_name.orEmpty(),
                        blockDate = client?.block_date.orEmpty(),
                        block = client?.block.orFalse(),
                        balance = client?.balance.or0(),
                        blockNote = client?.block_note.orEmpty(),
                        rating = client?.rating.or0(),
                        blockSource = client?.block_source.orEmpty(),
                        image = client?.image.orEmpty(),
                        blockExpiry = client?.block_expiry.orEmpty(),
                        brand = client?.brand.let(brandMapper),
                        createdAt = client?.created_at.orEmpty(),
                        creatorType = client?.creator_type.orEmpty(),
                        birthday = client?.birthday.orEmpty(),
                        gender = client?.gender.orEmpty()
                    )
                }

            ValidateAuthCodeModel(
                isClient = remote?.is_client.orFalse(),
                tokenType = remote?.token_type.orEmpty(),
                accessToken = remote?.access_token.orEmpty(),
                expiresIn = remote?.expires_in.or0(),
                client = remote?.client.let(clientMapper)
            )
        }
}