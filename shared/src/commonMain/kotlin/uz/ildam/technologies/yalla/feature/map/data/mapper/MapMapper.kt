package uz.ildam.technologies.yalla.feature.map.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.core.data.mapper.orFalse
import uz.ildam.technologies.yalla.feature.map.data.response.map.AddressNameResponse
import uz.ildam.technologies.yalla.feature.map.data.response.map.PolygonResponseItem
import uz.ildam.technologies.yalla.feature.map.data.response.map.SearchForAddressResponseItem
import uz.ildam.technologies.yalla.feature.map.domain.model.map.AddressModel
import uz.ildam.technologies.yalla.feature.map.domain.model.map.PolygonRemoteItem
import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel

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

    val addressMapper: Mapper<AddressNameResponse?, AddressModel> = { remote ->
        AddressModel(
            lat = remote?.lat.orEmpty(),
            lng = remote?.lng.orEmpty(),
            name = remote?.name
        )
    }

    val searchAddressItemMapper: Mapper<SearchForAddressResponseItem?, SearchForAddressItemModel> =
        { remote ->
            SearchForAddressItemModel(
                addressId = remote?.address_id.or0(),
                addressName = remote?.address_name.orEmpty(),
                db = remote?.db.orFalse(),
                distance = remote?.distance.or0(),
                lat = remote?.lat.or0(),
                lng = remote?.lng.or0(),
                name = remote?.name.orEmpty()
            )
        }
}