package uz.yalla.client.feature.order.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.feature.order.domain.model.response.PlaceModel
import uz.yalla.client.core.domain.model.type.PlaceType
import uz.yalla.client.service.places.response.PlaceRemoteModel

object PlacesMapper {
    val mapper: Mapper<PlaceRemoteModel?, PlaceModel> = { remote ->
        PlaceModel(
            id = remote?.id.or0(),
            name = remote?.address.orEmpty(),
            address = remote?.address.orEmpty(),
            coords = remote?.coords.let(coordsMapper),
            type = PlaceType.fromType(remote?.type.orEmpty()),
            enter = remote?.enter.orEmpty(),
            apartment = remote?.apartment.orEmpty(),
            floor = remote?.floor.orEmpty(),
            comment = remote?.comment.orEmpty(),
        )
    }

    private val coordsMapper: Mapper<PlaceRemoteModel.Coords?, PlaceModel.Coords> = { remote ->
        PlaceModel.Coords(
            lat = remote?.lat.or0(),
            lng = remote?.lng.or0()
        )
    }
}