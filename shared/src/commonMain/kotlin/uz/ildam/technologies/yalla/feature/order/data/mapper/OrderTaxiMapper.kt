package uz.ildam.technologies.yalla.feature.order.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.feature.order.data.response.order.OrderTaxiResponse
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderTaxiModel

object OrderTaxiMapper {
    val mapper: Mapper<OrderTaxiResponse?, OrderTaxiModel> = { remote ->
        OrderTaxiModel(
            orderId = remote?.order_id.or0()
        )
    }
}