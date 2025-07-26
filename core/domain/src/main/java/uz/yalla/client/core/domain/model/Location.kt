package uz.yalla.client.core.domain.model

data class Location(
    val name: String?,
    val point: MapPoint?,
    val addressId: Int?
) {
    fun mapToDestination() = Destination(
        name = this.name,
        point = this.point
    )
}

