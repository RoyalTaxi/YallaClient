package uz.ildam.technologies.yalla.feature.auth.data.response.auth

import kotlinx.serialization.Serializable
import uz.ildam.technologies.yalla.core.data.response.ClientRemoteModel

@Serializable
data class ValidateAuthCodeResponse(
    val is_client: Boolean?,
    val token_type: String?,
    val access_token: String?,
    val expires_in: Int?,
    val client: ClientRemoteModel?,
    val key: String?
)