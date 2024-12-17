package uz.ildam.technologies.yalla.feature.order.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.data.mapper.GetSettingMapper
import uz.ildam.technologies.yalla.feature.order.data.mapper.OrderTaxiMapper
import uz.ildam.technologies.yalla.feature.order.data.mapper.SearchCarMapper
import uz.ildam.technologies.yalla.feature.order.data.request.order.OrderTaxiRequest
import uz.ildam.technologies.yalla.feature.order.data.service.OrderService
import uz.ildam.technologies.yalla.feature.order.domain.model.request.OrderTaxiDto
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderTaxiModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SearchCarModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SettingModel
import uz.ildam.technologies.yalla.feature.order.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderService: OrderService
) : OrderRepository {
    override suspend fun orderTaxi(body: OrderTaxiDto): Result<OrderTaxiModel, DataError.Network> {
        return when (val result = orderService.orderTaxi(
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
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(result.data.result.let(OrderTaxiMapper.mapper))
        }
    }

    override suspend fun searchCar(
        lat: Double,
        lng: Double,
        tariffId: Int
    ): Result<SearchCarModel, DataError.Network> {
        return when (val result = orderService.searchCars(
            lat = lat,
            lng = lng,
            tariffId = tariffId
        )) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(result.data.result.let(SearchCarMapper.mapper))
        }
    }

    override suspend fun getSetting(): Result<SettingModel, DataError.Network> {
        return when (val result = orderService.getSetting()) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(result.data.result.let(GetSettingMapper.mapper))
        }
    }

    override suspend fun cancelRide(orderId: Int): Result<Unit, DataError.Network> {
        return when (val result = orderService.cancelRide(orderId)) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(Unit)
        }
    }

    override suspend fun cancelReason(orderId: Int): Result<Unit, DataError.Network> {
        return when (val result = orderService.cancelRide(orderId)) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(Unit)
        }
    }
}