package uz.yalla.client.service.auth.request.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUserRequest(
    val phone: String,
    val given_names: String,
    val sur_name: String,
    val gender: String,
    val birthday: String,
    val key: String
)