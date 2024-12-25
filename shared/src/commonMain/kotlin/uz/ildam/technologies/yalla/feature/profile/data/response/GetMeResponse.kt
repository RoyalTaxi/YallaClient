package uz.ildam.technologies.yalla.feature.profile.data.response

import kotlinx.serialization.Serializable
import uz.ildam.technologies.yalla.core.data.response.ClientRemoteModel

@Serializable
data class GetMeResponse(
    val client: ClientRemoteModel?
)