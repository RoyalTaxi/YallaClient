package uz.ildam.technologies.yalla.feature.auth.domain.model.auth

data class SendAuthCodeModel(
    val time: Int,
    val resultMessage: String
)