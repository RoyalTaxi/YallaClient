package uz.ildam.technologies.yalla.feature.order.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.feature.order.data.response.order.ActiveOrdersResponse
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.ActiveOrdersModel

object ActiveOrdersMapper {
    val mapper: Mapper<ActiveOrdersResponse?, ActiveOrdersModel> = { remote ->
        ActiveOrdersModel(
            list = remote?.list?.map(ShowOrderMapper.mapper).orEmpty()
        )
    }
}