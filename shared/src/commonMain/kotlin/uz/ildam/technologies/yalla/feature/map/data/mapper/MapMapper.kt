package uz.ildam.technologies.yalla.feature.map.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.feature.map.data.response.address.AddressResponse
import uz.ildam.technologies.yalla.feature.map.data.response.address.PolygonResponseItem
import uz.ildam.technologies.yalla.feature.map.domain.model.map.AddressModel
import uz.ildam.technologies.yalla.feature.map.domain.model.map.PolygonRemoteItem

object MapMapper {
    val polygonMapper: Mapper<PolygonResponseItem?, PolygonRemoteItem> = { remote ->
        PolygonRemoteItem(
            addressId = remote?.address_id.or0(),
            polygons = remote?.polygon?.map(polygonItemMapper).orEmpty()
        )
    }

    private val polygonItemMapper: Mapper<PolygonResponseItem.Polygon?, PolygonRemoteItem.Polygon> =
        { remote ->
            PolygonRemoteItem.Polygon(
                lat = remote?.lat.or0(),
                lng = remote?.lng.or0()
            )
        }

    val addressMapper: Mapper<AddressResponse?, AddressModel> = { remote ->
        AddressModel(
            lat = remote?.lat.orEmpty(),
            lng = remote?.lng.orEmpty(),
            name = remote?.name
        )
    }
}