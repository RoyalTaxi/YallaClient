package uz.ildam.technologies.yalla.feature.auth.data.response.register

import kotlinx.serialization.Serializable
import uz.ildam.technologies.yalla.core.data.response.ClientRemoteModel

@Serializable
data class RegisterResponse(
    val token_type: String?,
    val access_token: String?,
    val expires_in: String?,
    val client: ClientRemoteModel?
)