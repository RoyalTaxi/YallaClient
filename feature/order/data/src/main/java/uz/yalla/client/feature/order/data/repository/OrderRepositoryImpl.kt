package uz.yalla.client.feature.order.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.order.data.mapper.ActiveOrdersMapper
import uz.yalla.client.feature.order.data.mapper.GetSettingMapper
import uz.yalla.client.feature.order.data.mapper.OrderTaxiMapper
import uz.yalla.client.feature.order.data.mapper.SearchCarMapper
import uz.yalla.client.feature.order.data.mapper.ShowOrderMapper
import uz.yalla.client.feature.order.domain.model.request.OrderTaxiDto
import uz.yalla.client.feature.order.domain.model.response.order.ActiveOrdersModel
import uz.yalla.client.feature.order.domain.model.response.order.OrderTaxiModel
import uz.yalla.client.feature.order.domain.model.response.order.SearchCarModel
import uz.yalla.client.feature.order.domain.model.response.order.SettingModel
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.order.domain.repository.OrderRepository
import uz.yalla.client.service.order.request.order.OrderTaxiRequest
import uz.yalla.client.service.order.service.OrderApiService

class OrderRepositoryImpl(
    private val orderApiService: OrderApiService
) : OrderRepository {
    override suspend fun orderTaxi(body: OrderTaxiDto): Either<OrderTaxiModel, DataError.Network> {
        return when (val result = orderApiService.orderTaxi(
            OrderTaxiRequest(
                dont_call_me = body.dontCallMe,
                service = body.service,
                address_id = body.addressId,
                to_phone = body.toPhone,
                comment = body.comment,
                tariff_id = body.tariffId,
                tariff_options = body.tariffOptions,
                payment_type = body.paymentType,
                fixed_price = body.fixedPrice,
                card_id = body.cardId,
                addresses = body.addresses.map { address ->
                    OrderTaxiRequest.Address(
                        address_id = address.addressId,
                        lat = address.lat,
                        lng = address.lng,
                        name = address.name
                    )
                }
            )
        )) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(OrderTaxiMapper.mapper))
        }
    }

    override suspend fun searchCar(
        lat: Double,
        lng: Double,
        tariffId: Int
    ): Either<SearchCarModel, DataError.Network> {
        return when (val result = orderApiService.searchCars(
            lat = lat,
            lng = lng,
            tariffId = tariffId
        )) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(SearchCarMapper.mapper))
        }
    }

    override suspend fun getSetting(): Either<SettingModel, DataError.Network> {
        return when (val result = orderApiService.getSetting()) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(GetSettingMapper.mapper))
        }
    }

    override suspend fun cancelRide(orderId: Int): Either<Unit, DataError.Network> {
        return when (val result = orderApiService.cancelRide(orderId)) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(Unit)
        }
    }

    override suspend fun cancelReason(
        orderId: Int,
        reasonId: Int,
        reasonComment: String
    ): Either<Unit, DataError.Network> {
        return when (val result = orderApiService.cancelReason(
            orderId = orderId,
            reasonId = reasonId,
            reasonComment = reasonComment
        )) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(Unit)
        }
    }

    override suspend fun getShowOrder(orderId: Int): Either<ShowOrderModel, DataError.Network> {
        return when (val result = orderApiService.show(orderId)) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(ShowOrderMapper.mapper))
        }
    }

    override suspend fun rateTheRide(
        ball: Int,
        orderId: Int,
        comment: String
    ): Either<Unit, DataError.Network> {
        return when (val result = orderApiService.rateTheRide(
            ball = ball,
            orderId = orderId,
            comment = comment
        )) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(Unit)
        }
    }

    override suspend fun getActiveOrders(): Either<ActiveOrdersModel, DataError.Network> {
        return when (val result = orderApiService.getActiveOrders()) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(ActiveOrdersMapper.mapper))
        }
    }
}