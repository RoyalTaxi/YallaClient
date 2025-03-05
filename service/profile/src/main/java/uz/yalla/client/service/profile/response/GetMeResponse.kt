package uz.yalla.client.service.profile.response

import kotlinx.serialization.Serializable
import uz.yalla.client.core.service.model.ClientRemoteModel

@Serializable
data class GetMeResponse(
    val client: ClientRemoteModel?
)