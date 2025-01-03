package uz.ildam.technologies.yalla.feature.map.domain.model.response.map

data class AddressModel(
    val db: Boolean?,
    val displayName: String?,
    val id: Long?,
    val lat: Double,
    val lng: Double
)