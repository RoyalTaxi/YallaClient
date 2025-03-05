package uz.yalla.client.feature.auth.domain.model.register

import uz.yalla.client.core.domain.model.Client

data class RegisterModel(
    val tokenType: String,
    val accessToken: String,
    val expiresIn: String,
    val client: Client?
)