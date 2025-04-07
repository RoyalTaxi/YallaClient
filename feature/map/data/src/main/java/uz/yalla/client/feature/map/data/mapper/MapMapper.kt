package uz.yalla.client.feature.map.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.data.mapper.orFalse
import uz.yalla.client.feature.map.domain.model.response.GetRoutingModel
import uz.yalla.client.feature.map.domain.model.response.PolygonRemoteItem
import uz.yalla.client.feature.map.domain.model.response.SearchForAddressItemModel
import uz.yalla.client.feature.map.domain.model.response.SecondaryAddressItemModel
import uz.yalla.client.feature.order.domain.model.response.PlaceNameModel
import uz.yalla.client.core.domain.model.type.PlaceType
import uz.yalla.client.service.map.response.PlaceNameResponse
import uz.yalla.client.service.map.response.GetRoutingResponse
import uz.yalla.client.service.map.response.PolygonResponseItem
import uz.yalla.client.service.map.response.SearchForAddressResponseItem
import uz.yalla.client.service.map.response.SecondaryAddressResponseItem

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

    val addressMapper: Mapper<PlaceNameResponse?, PlaceNameModel> = { remote ->
        PlaceNameModel(
            db = remote?.db.orFalse(),
            displayName = remote?.display_name.orEmpty(),
            id = remote?.id,
            lat = remote?.lat.or0(),
            lng = remote?.lng.or0()
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

    val routingMapper: Mapper<GetRoutingResponse?, GetRoutingModel> = { remote ->
        GetRoutingModel(
            distance = remote?.distance.or0(),
            duration = remote?.duration.or0(),
            routing = remote?.routing?.map(routingItemMapper).orEmpty()
        )
    }

    private val routingItemMapper: Mapper<GetRoutingResponse.Routing?, GetRoutingModel.Routing> =
        { remote ->
            GetRoutingModel.Routing(
                lat = remote?.lat.or0(),
                lng = remote?.lng.or0()
            )
        }

    val secondaryAddressItemMapper: Mapper<SecondaryAddressResponseItem?, SecondaryAddressItemModel> =
        { remote ->
            SecondaryAddressItemModel(
                distance = remote?.distance.or0(),
                lat = remote?.lat.or0(),
                lng = remote?.lng.or0(),
                name = remote?.name.orEmpty(),
                addressName = remote?.address_name.orEmpty(),
                type = PlaceType.fromType(remote?.type)
            )
        }
}