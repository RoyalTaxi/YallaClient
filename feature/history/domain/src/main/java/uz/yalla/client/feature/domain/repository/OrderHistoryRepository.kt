package uz.yalla.client.feature.domain.repository

import app.cash.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.domain.model.OrderHistoryModel
import uz.yalla.client.feature.domain.model.OrdersHistoryModel

interface OrderHistoryRepository {
    fun getOrdersHistory(): Flow<PagingData<OrdersHistoryModel>>
    suspend fun getOrderHistory(orderId: Int): Either<OrderHistoryModel, DataError.Network>
}