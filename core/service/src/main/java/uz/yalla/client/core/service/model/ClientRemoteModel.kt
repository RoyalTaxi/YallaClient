package uz.yalla.client.core.service.model

import kotlinx.serialization.Serializable

@Serializable
data class ClientRemoteModel(
    val phone: String?,
    val given_names: String?,
    val sur_name: String?,
    val image: String?,
    val birthday: String?,
    val balance: Int?,
    val gender: String?
)
