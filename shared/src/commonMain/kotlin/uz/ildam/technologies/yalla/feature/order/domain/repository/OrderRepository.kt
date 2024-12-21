package uz.ildam.technologies.yalla.feature.order.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.domain.model.request.OrderTaxiDto
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderTaxiModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SearchCarModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SettingModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.ShowOrderModel


interface OrderRepository {
    suspend fun orderTaxi(body: OrderTaxiDto): Result<OrderTaxiModel, DataError.Network>

    suspend fun searchCar(
        lat: Double,
        lng: Double,
        tariffId: Int
    ): Result<SearchCarModel, DataError.Network>

    suspend fun getSetting(): Result<SettingModel, DataError.Network>

    suspend fun cancelRide(orderId: Int): Result<Unit, DataError.Network>

    suspend fun cancelReason(
        orderId: Int,
        reasonId: Int,
        reasonComment: String
    ): Result<Unit, DataError.Network>

    suspend fun getShowOrder(
        orderId: Int
    ): Result<ShowOrderModel, DataError.Network>

    suspend fun rateTheRide(
        ball: Int,
        orderId: Int,
        comment: String
    ): Result<Unit, DataError.Network>
}