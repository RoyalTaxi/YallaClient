package uz.ildam.technologies.yalla.core.data.mapper

import uz.ildam.technologies.yalla.core.data.response.ClientRemoteModel
import uz.ildam.technologies.yalla.core.domain.model.ClientModel

object ClientMapper {
    val clientMapper: Mapper<ClientRemoteModel?, ClientModel> =
        { client ->
            ClientModel(
                id = client?.id.or0(),
                phone = client?.phone.orEmpty(),
                givenNames = client?.given_names.orEmpty(),
                surname = client?.sur_name.orEmpty(),
                image = client?.image.orEmpty(),
                birthday = client?.birthday.orEmpty(),
                gender = client?.gender.orEmpty()
            )
        }
}