package uz.ildam.technologies.yalla.feature.auth.data.request.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val phone: String,
    val given_names: String,
    val sur_name: String,
    val gender: String,
    val birthday: String,
    val key: String
)