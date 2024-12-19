package uz.ildam.technologies.yalla.core.domain.model

data class ClientModel(
    val id: Long,
    val phone: String,
    val givenNames: String,
    val fatherName: String,
    val surname: String,
    val block: Boolean,
    val balance: Int,
    val blockNote: String,
    val rating: Double,
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
        val id: Long,
        val name: String
    )
}