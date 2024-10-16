package uz.ildam.technologies.yalla.feature.auth.domain.model

data class VerifyAuthCodeModel(
    val isClient: Boolean,
    val tokenType: String,
    val accessToken: String,
    val expiresIn: Int,
    val client: Client,
    val key: String
) {
    data class Client(
        val id: Int,
        val phone: String,
        val givenNames: String,
        val fatherName: String,
        val surname: String,
        val block: Boolean,
        val balance: Int,
        val blockNote: String,
        val rating: Int,
        val blockDate: String,
        val blockSource: String,
        val image: String,
        val blockExpiry: String,
        val brand: Brand,
        val createdAt: String,
        val creatorType: String,
        val birthday: String,
        val gender: String
    ) {
        data class Brand(
            val id: Int,
            val name: String
        )
    }
}