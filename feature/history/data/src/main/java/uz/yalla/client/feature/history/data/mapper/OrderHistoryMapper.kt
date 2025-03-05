package uz.yalla.client.feature.history.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.formations.TimeFormation.toFormattedDate
import uz.yalla.client.core.domain.formations.TimeFormation.toFormattedTime
import uz.yalla.client.feature.domain.model.OrderHistoryModel
import uz.yalla.client.service.history.response.OrderHistoryResponse

object OrderHistoryMapper {
    val mapper: Mapper<OrderHistoryResponse?, OrderHistoryModel> = { remote ->
        OrderHistoryModel(
            comment = remote?.comment.orEmpty(),
            date = remote?.date_time?.toFormattedDate().orEmpty(),
            time = remote?.date_time?.toFormattedTime().orEmpty(),
            executor = remote?.executor?.let(executorMapper) ?: defaultExecutor(),
            id = remote?.id.or0(),
            number = remote?.number.or0(),
            paymentType = remote?.payment_type.orEmpty(),
            service = remote?.service.orEmpty(),
            status = remote?.status.orEmpty(),
            taxi = remote?.taxi?.let(taxiMapper) ?: defaultTaxi(),
            track = remote?.track?.map(trackMapper).orEmpty()
        )
    }

    private val executorMapper: Mapper<OrderHistoryResponse.Executor?, OrderHistoryModel.Executor> =
        { remote ->
            OrderHistoryModel.Executor(
                cords = remote?.coords?.let(coordinatesMapper) ?: defaultCoordinatesWithHeading(),
                driver = remote?.driver?.let(driverMapper) ?: defaultDriver(),
                fatherName = remote?.father_name.orEmpty(),
                givenNames = remote?.given_names.orEmpty(),
                id = remote?.id.or0(),
                phone = remote?.phone.orEmpty(),
                surName = remote?.sur_name.orEmpty()
            )
        }

    private val coordinatesMapper: Mapper<OrderHistoryResponse.Executor.Coordinates?, OrderHistoryModel.Executor.Coordinates> =
        { remote ->
            OrderHistoryModel.Executor.Coordinates(
                heading = remote?.heading.or0(),
                lat = remote?.lat.or0(),
                lng = remote?.lng.or0()
            )
        }

    private val driverMapper: Mapper<OrderHistoryResponse.Executor.Driver?, OrderHistoryModel.Executor.Driver> =
        { remote ->
            OrderHistoryModel.Executor.Driver(
                callSign = remote?.callsign.orEmpty(),
                color = remote?.color?.let(colorMapper) ?: defaultColor(),
                id = remote?.id.or0(),
                mark = remote?.mark.orEmpty(),
                model = remote?.model.orEmpty(),
                stateNumber = remote?.state_number.orEmpty()
            )
        }

    private val colorMapper: Mapper<OrderHistoryResponse.Executor.Driver.Color?, OrderHistoryModel.Executor.Driver.Color> =
        { remote ->
            OrderHistoryModel.Executor.Driver.Color(
                color = remote?.color.orEmpty(),
                name = remote?.name.orEmpty()
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
                services = remote?.services.orEmpty(),
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

    private val trackMapper: Mapper<OrderHistoryResponse.Track?, OrderHistoryModel.Track> =
        { remote ->
            OrderHistoryModel.Track(
                lat = remote?.lat.or0(),
                lng = remote?.lng.or0(),
                speed = remote?.speed.or0(),
                status = remote?.status.orEmpty(),
                time = remote?.time.or0()
            )
        }

    // Default objects to handle null gracefully
    private fun defaultExecutor() = OrderHistoryModel.Executor(
        cords = defaultCoordinatesWithHeading(),
        driver = defaultDriver(),
        fatherName = "",
        givenNames = "",
        id = 0,
        phone = "",
        surName = ""
    )

    private fun defaultDriver() = OrderHistoryModel.Executor.Driver(
        callSign = "",
        color = defaultColor(),
        id = 0,
        mark = "",
        model = "",
        stateNumber = ""
    )

    private fun defaultColor() = OrderHistoryModel.Executor.Driver.Color(
        color = "",
        name = ""
    )

    private fun defaultTaxi() = OrderHistoryModel.Taxi(
        bonusAmount = 0,
        clientTotalPrice = 0,
        distance = 0.0,
        fixedPrice = false,
        routes = emptyList(),
        routesForRobot = emptyList(),
        services = "",
        startPrice = 0,
        tariff = "",
        totalPrice = 0,
        useTheBonus = false
    )

    private fun defaultCoordinatesWithHeading() = OrderHistoryModel.Executor.Coordinates(
        heading = 0.0,
        lat = 0.0,
        lng = 0.0
    )

    private fun defaultCoordinates() = OrderHistoryModel.Taxi.Route.Coordinates(
        lat = 0.0,
        lng = 0.0
    )
}