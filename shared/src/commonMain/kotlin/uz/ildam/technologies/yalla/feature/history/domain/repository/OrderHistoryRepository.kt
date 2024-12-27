package uz.ildam.technologies.yalla.feature.history.domain.repository

import app.cash.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryModel
import uz.ildam.technologies.yalla.feature.history.domain.model.OrdersHistoryModel

interface OrderHistoryRepository {
    fun getOrdersHistory(): Flow<PagingData<OrdersHistoryModel>>
    suspend fun getOrderHistory(orderId: Int): Either<OrderHistoryModel, DataError.Network>
}