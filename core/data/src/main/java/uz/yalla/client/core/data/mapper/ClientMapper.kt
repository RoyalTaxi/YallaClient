package uz.yalla.client.core.data.mapper

import uz.yalla.client.core.domain.model.Client
import uz.yalla.client.core.service.model.ClientRemoteModel

object ClientMapper {
    val clientMapper: Mapper<ClientRemoteModel?, Client> =
        { client ->
            Client(
                phone = client?.phone.orEmpty(),
                givenNames = client?.given_names.orEmpty(),
                surname = client?.sur_name.orEmpty(),
                image = client?.image.orEmpty(),
                birthday = client?.birthday.orEmpty(),
                gender = client?.gender.orEmpty()
            )
        }
}