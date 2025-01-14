package uz.ildam.technologies.yalla.core.data.mapper

import uz.ildam.technologies.yalla.core.data.response.ClientRemoteModel
import uz.ildam.technologies.yalla.core.domain.model.Client

object ClientMapper {
    val clientMapper: Mapper<ClientRemoteModel?, Client> =
        { client ->
            Client(
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