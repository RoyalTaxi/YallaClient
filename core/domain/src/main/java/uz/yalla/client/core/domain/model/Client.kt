package uz.yalla.client.core.domain.model

data class Client(
    val phone: String,
    val givenNames: String,
    val surname: String,
    val image: String,
    val birthday: String,
    val balance: Int,
    val gender: String
)