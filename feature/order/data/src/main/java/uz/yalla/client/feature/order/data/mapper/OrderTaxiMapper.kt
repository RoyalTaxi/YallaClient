package uz.yalla.client.feature.order.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.ildam.technologies.yalla.feature.order.data.response.order.OrderTaxiResponse

object OrderTaxiMapper {
    val mapper: Mapper<OrderTaxiResponse?, OrderTaxiModel> = { remote ->
        OrderTaxiModel(
            orderId = remote?.order_id.or0()
        )
    }
}