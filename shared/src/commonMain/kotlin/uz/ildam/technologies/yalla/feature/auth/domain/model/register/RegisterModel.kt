package uz.ildam.technologies.yalla.feature.auth.domain.model.register

import uz.ildam.technologies.yalla.core.domain.model.Client

data class RegisterModel(
    val tokenType: String,
    val accessToken: String,
    val expiresIn: String,
    val client: Client?
)