package uz.yalla.client.feature.history.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.domain.model.OrdersHistoryModel
import uz.yalla.client.service.history.response.OrdersHistoryResponseItem

object OrdersHistoryMapper {
    val mapper: Mapper<OrdersHistoryResponseItem?, OrdersHistoryModel> = { remote ->
        OrdersHistoryModel(
            dateTime = remote?.date_time.or0(),
            id = remote?.id.or0(),
            service = remote?.service.orEmpty(),
            status = OrderStatus.from(remote?.status),
            taxi = remote?.taxi.let(taxiMapper),
        )
    }

    private val taxiMapper: Mapper<OrdersHistoryResponseItem.Taxi?, OrdersHistoryModel.Taxi> =
        { remote ->
            OrdersHistoryModel.Taxi(
                routes = remote?.routes?.map(routeMapper).orEmpty(),
                totalPrice = remote?.total_price.or0().toString(),
            )
        }

    private val routeMapper: Mapper<OrdersHistoryResponseItem.Taxi.Route?, OrdersHistoryModel.Taxi.Route> =
        { remote ->
            OrdersHistoryModel.Taxi.Route(
                cords = remote?.coords.let(cordMapper),
                fullAddress = remote?.full_address.orEmpty(),
                index = remote?.index.or0()
            )
        }

    private val cordMapper: Mapper<OrdersHistoryResponseItem.Taxi.Route.Cords?, OrdersHistoryModel.Taxi.Route.Cords> =
        { remote ->
            OrdersHistoryModel.Taxi.Route.Cords(
                lat = remote?.lat.or0(),
                lng = remote?.lng.or0()
            )
        }
}