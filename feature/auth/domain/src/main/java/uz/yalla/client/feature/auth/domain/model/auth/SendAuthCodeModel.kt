package uz.yalla.client.feature.auth.domain.model.auth

data class SendAuthCodeModel(
    val time: Int,
    val resultMessage: String
)