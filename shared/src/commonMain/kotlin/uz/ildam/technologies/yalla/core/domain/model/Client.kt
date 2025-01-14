package uz.ildam.technologies.yalla.core.domain.model

data class Client(
    val id: Long,
    val phone: String,
    val givenNames: String,
    val surname: String,
    val image: String,
    val birthday: String,
    val gender: String
)