package uz.yalla.client.core.service.model

import kotlinx.serialization.Serializable

@Serializable
data class ClientRemoteModel(
    val id: Int?,
    val phone: String?,
    val given_names: String?,
    val sur_name: String?,
    val image: String?,
    val birthday: String?,
    val gender: String?
)
