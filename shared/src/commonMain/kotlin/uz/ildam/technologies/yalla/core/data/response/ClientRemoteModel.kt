package uz.ildam.technologies.yalla.core.data.response

import kotlinx.serialization.Serializable

@Serializable
data class ClientRemoteModel(
    val id: Long?,
    val phone: String?,
    val given_names: String?,
    val sur_name: String?,
    val image: String?,
    val birthday: String?,
    val gender: String?
)
