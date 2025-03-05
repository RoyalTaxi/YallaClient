package uz.yalla.client.feature.auth.domain.model.auth

import uz.yalla.client.core.domain.model.Client

data class VerifyAuthCodeModel(
    val isClient: Boolean,
    val tokenType: String,
    val accessToken: String,
    val expiresIn: Int,
    val client: Client?,
    val key: String
)