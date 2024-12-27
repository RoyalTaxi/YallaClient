package uz.ildam.technologies.yalla.feature.order.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.order.domain.model.request.OrderTaxiDto
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.ActiveOrdersModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderTaxiModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SearchCarModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SettingModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.ShowOrderModel


interface OrderRepository {
    suspend fun orderTaxi(body: OrderTaxiDto): Either<OrderTaxiModel, DataError.Network>

    suspend fun searchCar(
        lat: Double,
        lng: Double,
        tariffId: Int
    ): Either<SearchCarModel, DataError.Network>

    suspend fun getSetting(): Either<SettingModel, DataError.Network>

    suspend fun cancelRide(orderId: Int): Either<Unit, DataError.Network>

    suspend fun cancelReason(
        orderId: Int,
        reasonId: Int,
        reasonComment: String
    ): Either<Unit, DataError.Network>

    suspend fun getShowOrder(
        orderId: Int
    ): Either<ShowOrderModel, DataError.Network>

    suspend fun rateTheRide(
        ball: Int,
        orderId: Int,
        comment: String
    ): Either<Unit, DataError.Network>

    suspend fun getActiveOrders(): Either<ActiveOrdersModel, DataError.Network>
}