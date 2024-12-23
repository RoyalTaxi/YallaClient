package uz.ildam.technologies.yalla.feature.addresses.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.feature.addresses.data.response.AddressRemoteModel
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressModel
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressType

object AddressesMapper {
    val mapper: Mapper<AddressRemoteModel?, AddressModel> = { remote ->
        AddressModel(
            id = remote?.id.or0(),
            name = remote?.address.orEmpty(),
            address = remote?.address.orEmpty(),
            coords = remote?.coords.let(coordsMapper),
            type = AddressType.fromType(remote?.type.orEmpty()),
            enter = remote?.enter.orEmpty(),
            apartment = remote?.apartment.orEmpty(),
            floor = remote?.floor.orEmpty(),
            comment = remote?.comment.orEmpty(),
            createdAt = remote?.created_at.orEmpty()
        )
    }

    private val coordsMapper: Mapper<AddressRemoteModel.Coords?, AddressModel.Coords> = { remote ->
        AddressModel.Coords(
            lat = remote?.lat.or0(),
            lng = remote?.lng.or0()
        )
    }
}