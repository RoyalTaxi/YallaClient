package uz.ildam.technologies.yalla.feature.addresses.domain.model.response

data class AddressModel(
    val id: Int,
    val name: String,
    val address: String,
    val coords: Coords,
    val type: AddressType,
    val enter: String,
    val apartment: String,
    val floor: String,
    val comment: String,
    val createdAt: String
) {
    data class Coords(
        val lat: Double,
        val lng: Double
    )
}