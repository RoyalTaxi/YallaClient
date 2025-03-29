package uz.yalla.client.core.data.mapper

import uz.yalla.client.core.domain.model.ServiceModel
import uz.yalla.client.core.service.model.ServiceRemoteModel

object ServiceMapper {
    val mapper: Mapper<ServiceRemoteModel?, ServiceModel> = { remote ->
        ServiceModel(
            cost = remote?.cost.or0(),
            costType = remote?.costType ?: ServiceModel.COST_TYPE_COST,
            id = remote?.id.or0(),
            name = remote?.name.orEmpty()
        )
    }
}