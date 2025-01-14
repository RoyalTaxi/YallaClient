package uz.ildam.technologies.yalla.feature.auth.domain.model.auth

import uz.ildam.technologies.yalla.core.domain.model.Client

data class VerifyAuthCodeModel(
    val isClient: Boolean,
    val tokenType: String,
    val accessToken: String,
    val expiresIn: Int,
    val client: Client?,
    val key: String
)