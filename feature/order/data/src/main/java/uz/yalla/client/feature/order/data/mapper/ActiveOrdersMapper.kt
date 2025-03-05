package uz.yalla.client.feature.order.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.feature.order.data.response.order.ActiveOrdersResponse

object ActiveOrdersMapper {
    val mapper: Mapper<ActiveOrdersResponse?, ActiveOrdersModel> = { remote ->
        ActiveOrdersModel(
            list = remote?.list?.map(ShowOrderMapper.mapper).orEmpty()
        )
    }
}