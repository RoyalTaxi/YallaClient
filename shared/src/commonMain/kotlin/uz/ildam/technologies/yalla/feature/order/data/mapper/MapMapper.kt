package uz.ildam.technologies.yalla.feature.order.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.feature.order.data.response.map.PolygonResponseItem
import uz.ildam.technologies.yalla.feature.order.domain.model.map.PolygonRemoteItem

object MapMapper {
    val mapper: Mapper<PolygonResponseItem?, PolygonRemoteItem> = { remote ->
        PolygonRemoteItem(
            addressId = remote?.address_id.or0(),
            polygons = remote?.polygons?.map(polygonMapper).orEmpty()
        )
    }

    private val polygonMapper: Mapper<PolygonResponseItem.Polygon?, PolygonRemoteItem.Polygon> =
        { remote ->
            PolygonRemoteItem.Polygon(
                lat = remote?.lat.or0(),
                lng = remote?.lng.or0()
            )
        }
}