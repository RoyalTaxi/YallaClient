package uz.yalla.client.feature.order.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.data.mapper.orFalse
import uz.yalla.client.core.domain.formations.TimeFormation.toFormattedTime
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.service.order.response.order.ShowOrderResponse

object ShowOrderMapper {
    val mapper: Mapper<ShowOrderResponse?, ShowOrderModel> = { remote ->
        ShowOrderModel(
            comment = remote?.comment.orEmpty(),
            dateTime = remote?.date_time?.toFormattedTime().orEmpty(),
            executor = remote?.executor.let(executorMapper),
            id = remote?.id.or0(),
            paymentType = remote?.payment_type.orEmpty(),
            service = remote?.service.orEmpty(),
            status = OrderStatus.from(remote?.status.orEmpty()),
            statusTime = remote?.status_time?.map(statusTimeMapper).orEmpty(),
            taxi = remote?.taxi.let(taxiMapper)
        )
    }

    private val executorMapper: Mapper<ShowOrderResponse.ExecutorData?, ShowOrderModel.Executor> =
        { remote ->
            ShowOrderModel.Executor(
                coords = remote?.coords.let(coordsMapper),
                driver = remote?.driver.let(driverMapper),
                fatherName = remote?.father_name.orEmpty(),
                givenNames = remote?.given_names.orEmpty(),
                id = remote?.id.or0(),
                phone = remote?.phone.orEmpty(),
                surName = remote?.sur_name.orEmpty()
            )
        }

    private val coordsMapper: Mapper<ShowOrderResponse.ExecutorData.CoordsData?, ShowOrderModel.Executor.Coords> =
        { remote ->
            ShowOrderModel.Executor.Coords(
                heading = remote?.heading.or0(),
                lat = remote?.lat.or0(),
                lng = remote?.lng.or0()
            )
        }

    private val driverMapper: Mapper<ShowOrderResponse.ExecutorData.DriverData?, ShowOrderModel.Executor.Driver> =
        { remote ->
            ShowOrderModel.Executor.Driver(
                callsign = remote?.callsign.orEmpty(),
                color = remote?.color.let(colorMapper),
                id = remote?.id.or0(),
                mark = remote?.mark.orEmpty(),
                model = remote?.model.orEmpty(),
                stateNumber = remote?.state_number.orEmpty()
            )
        }

    private val colorMapper: Mapper<ShowOrderResponse.ExecutorData.DriverData.ColorData?, ShowOrderModel.Executor.Driver.Color> =
        { remote ->
            ShowOrderModel.Executor.Driver.Color(
                color = remote?.color.orEmpty(),
                name = remote?.name.orEmpty()
            )
        }

    private val statusTimeMapper: Mapper<ShowOrderResponse.StatusTimeData?, ShowOrderModel.StatusTime> =
        { remote ->
            ShowOrderModel.StatusTime(
                status = remote?.status.orEmpty(),
                time = remote?.time.or0()
            )
        }

    private val taxiMapper: Mapper<ShowOrderResponse.TaxiData?, ShowOrderModel.Taxi> = { remote ->
        ShowOrderModel.Taxi(
            clientTotalPrice = remote?.client_total_price.or0(),
            distance = remote?.distance.or0(),
            fixedPrice = remote?.fixed_price.orFalse(),
            routes = remote?.routes?.map(routeMapper).orEmpty(),
            services = remote?.services.orEmpty(),
            startPrice = remote?.start_price.or0(),
            tariff = remote?.tariff.orEmpty(),
            totalPrice = remote?.total_price.or0()
        )
    }

    private val routeMapper: Mapper<ShowOrderResponse.TaxiData.RouteData?, ShowOrderModel.Taxi.Route> =
        { remote ->
            ShowOrderModel.Taxi.Route(
                coords = remote?.coords.let(routeCoordsMapper),
                fullAddress = remote?.full_address.orEmpty(),
                index = remote?.index.or0()
            )
        }

    private val routeCoordsMapper: Mapper<ShowOrderResponse.TaxiData.RouteData.CoordsData?, ShowOrderModel.Taxi.Route.Coords> =
        { remote ->
            ShowOrderModel.Taxi.Route.Coords(
                lat = remote?.lat.or0(),
                lng = remote?.lng.or0()
            )
        }
}