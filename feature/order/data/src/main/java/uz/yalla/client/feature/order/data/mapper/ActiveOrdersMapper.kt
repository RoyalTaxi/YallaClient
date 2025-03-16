package uz.yalla.client.feature.order.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.feature.order.domain.model.response.order.ActiveOrdersModel
import uz.yalla.client.service.order.response.order.ActiveOrdersResponse

object ActiveOrdersMapper {
    val mapper: Mapper<ActiveOrdersResponse?, ActiveOrdersModel> = { remote ->
        ActiveOrdersModel(
            list = remote?.list?.map(ShowOrderMapper.mapper).orEmpty()
        )
    }
}