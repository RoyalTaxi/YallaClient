package uz.yalla.client.service.auth.response.register

import kotlinx.serialization.Serializable
import uz.yalla.client.core.service.model.ClientRemoteModel

@Serializable
data class RegisterResponse(
    val token_type: String?,
    val access_token: String?,
    val expires_in: String?,
    val client: ClientRemoteModel?
)