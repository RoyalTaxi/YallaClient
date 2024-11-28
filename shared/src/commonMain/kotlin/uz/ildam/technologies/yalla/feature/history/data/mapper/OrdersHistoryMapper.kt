package uz.ildam.technologies.yalla.feature.history.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.core.data.mapper.orFalse
import uz.ildam.technologies.yalla.feature.history.data.response.OrdersHistoryResponseItem
import uz.ildam.technologies.yalla.feature.history.domain.model.OrdersHistoryModel

object OrdersHistoryMapper {
    val mapper: Mapper<OrdersHistoryResponseItem?, OrdersHistoryModel> = { remote ->
        OrdersHistoryModel(
            dateTime = remote?.date_time.or0(),
            id = remote?.id.or0(),
            service = remote?.service.orEmpty(),
            status = remote?.status.orEmpty(),
            taxi = remote?.taxi.let(taxiMapper),

        )
    }

    private val taxiMapper: Mapper<OrdersHistoryResponseItem.Taxi?, OrdersHistoryModel.Taxi> =
        { remote ->
            OrdersHistoryModel.Taxi(
                bonusAmount = remote?.bonus_amount.or0(),
                clientTotalPrice = remote?.client_total_price.or0(),
                distance = remote?.distance.or0(),
                fixedPrice = remote?.fixed_price.orFalse(),
                routes = remote?.routes?.map(routeMapper).orEmpty(),
                startPrice = remote?.start_price.or0(),
                tariff = remote?.tariff.orEmpty(),
                tariffCategoryId = remote?.tariff_category_id.or0(),
                totalPrice = remote?.total_price.or0().toString(),
                useTheBonus = remote?.use_the_bonus.orFalse()
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