package uz.ildam.technologies.yalla.feature.map.domain.model.map

data class AddressModel(
    val db: Boolean?,
    val displayName: String?,
    val id: Int?,
    val lat: Double,
    val lng: Double
)