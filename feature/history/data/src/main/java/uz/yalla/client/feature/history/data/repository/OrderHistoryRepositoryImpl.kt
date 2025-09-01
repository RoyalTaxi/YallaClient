package uz.yalla.client.feature.history.data.repository


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.yalla.client.feature.history.data.mapper.OrderHistoryMapper
import uz.yalla.client.feature.history.data.paging.OrdersHistoryPagingSource
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.data.ext.mapResult
import uz.yalla.client.feature.domain.model.OrderHistoryModel
import uz.yalla.client.feature.domain.model.OrdersHistoryModel
import uz.yalla.client.feature.domain.repository.OrderHistoryRepository
import uz.yalla.client.service.history.service.OrdersHistoryApiService

class OrderHistoryRepositoryImpl(
    private val service: OrdersHistoryApiService
) : OrderHistoryRepository {
    override fun getOrdersHistory(): Flow<PagingData<OrdersHistoryModel>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { OrdersHistoryPagingSource(service) }
    ).flow

    override suspend fun getOrderHistory(orderId: Int): Either<OrderHistoryModel, DataError.Network> =
        service.getOrder(orderId).mapResult(OrderHistoryMapper.mapper)
}
