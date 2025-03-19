package uz.yalla.client.service.auth.response.auth

import kotlinx.serialization.Serializable
import uz.yalla.client.core.service.model.ClientRemoteModel

@Serializable
data class ValidateAuthCodeResponse(
    val is_client: Boolean?,
    val token_type: String?,
    val access_token: String?,
    val client: ClientRemoteModel?,
    val key: String?
)