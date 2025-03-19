package uz.yalla.client.feature.auth.domain.model.register

data class RegisterModel(
    val tokenType: String,
    val accessToken: String
)