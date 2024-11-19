package uz.ildam.technologies.yalla.feature.history.domain.repository

import app.cash.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryModel

interface OrderHistoryRepository {
    fun getOrdersHistory(): Flow<PagingData<OrderHistoryModel>>
}