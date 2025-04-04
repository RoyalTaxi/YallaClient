package uz.yalla.client.feature.history.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.ServiceMapper
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.formations.TimeFormation.toFormattedDate
import uz.yalla.client.core.domain.formations.TimeFormation.toFormattedTime
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.domain.model.OrderHistoryModel
import uz.yalla.client.service.history.response.OrderHistoryResponse

object OrderHistoryMapper {
    val mapper: Mapper<OrderHistoryResponse?, OrderHistoryModel> = { remote ->
        OrderHistoryModel(
            date = remote?.date_time?.toFormattedDate().orEmpty(),
            time = remote?.date_time?.toFormattedTime().orEmpty(),
            executor = remote?.executor?.let(executorMapper) ?: defaultExecutor(),
            status = OrderStatus.from(remote?.status),
            taxi = remote?.taxi?.let(taxiMapper) ?: defaultTaxi(),
        )
    }

    private val executorMapper: Mapper<OrderHistoryResponse.Executor?, OrderHistoryModel.Executor> =
        { remote ->
            OrderHistoryModel.Executor(
                driver = remote?.driver?.let(driverMapper) ?: defaultDriver()
            )
        }

    private val driverMapper: Mapper<OrderHistoryResponse.Executor.Driver?, OrderHistoryModel.Executor.Driver> =
        { remote ->
            OrderHistoryModel.Executor.Driver(
                mark = remote?.mark.orEmpty(),
                model = remote?.model.orEmpty(),
                stateNumber = remote?.state_number.orEmpty()
            )
        }

    private val taxiMapper: Mapper<OrderHistoryResponse.Taxi?, OrderHistoryModel.Taxi> =
        { remote ->
            OrderHistoryModel.Taxi(
                bonusAmount = remote?.bonus_amount.or0(),
                clientTotalPrice = remote?.client_total_price.or0(),
                distance = remote?.distance.or0(),
                fixedPrice = remote?.fixed_price ?: false,
                routes = remote?.routes?.map(routeMapper).orEmpty(),
                routesForRobot = remote?.routes_for_robot?.map(routeMapper).orEmpty(),
                services = remote?.services?.map(ServiceMapper.mapper) ?: emptyList(),
                startPrice = remote?.start_price.or0(),
                tariff = remote?.tariff.orEmpty(),
                totalPrice = remote?.total_price.or0(),
                useTheBonus = remote?.use_the_bonus ?: false
            )
        }

    private val routeMapper: Mapper<OrderHistoryResponse.Taxi.Route?, OrderHistoryModel.Taxi.Route> =
        { remote ->
            OrderHistoryModel.Taxi.Route(
                cords = remote?.coords?.let(routeCoordinatesMapper) ?: defaultCoordinates(),
                fullAddress = remote?.full_address.orEmpty(),
                index = remote?.index.or0()
            )
        }

    private val routeCoordinatesMapper: Mapper<OrderHistoryResponse.Taxi.Route.Coordinates?, OrderHistoryModel.Taxi.Route.Coordinates> =
        { remote ->
            OrderHistoryModel.Taxi.Route.Coordinates(
                lat = remote?.lat.or0(),
                lng = remote?.lng.or0()
            )
        }

    private fun defaultExecutor() = OrderHistoryModel.Executor(
        driver = defaultDriver()
    )

    private fun defaultDriver() = OrderHistoryModel.Executor.Driver(
        mark = "",
        model = "",
        stateNumber = ""
    )

    private fun defaultTaxi() = OrderHistoryModel.Taxi(
        bonusAmount = 0,
        clientTotalPrice = 0,
        distance = 0.0,
        fixedPrice = false,
        routes = emptyList(),
        routesForRobot = emptyList(),
        services = emptyList(),
        startPrice = 0,
        tariff = "",
        totalPrice = 0,
        useTheBonus = false
    )

    private fun defaultCoordinates() = OrderHistoryModel.Taxi.Route.Coordinates(
        lat = 0.0,
        lng = 0.0
    )
}