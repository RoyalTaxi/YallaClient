package uz.ildam.technologies.yalla.feature.history.data.repository


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.history.data.mapper.OrderHistoryMapper
import uz.ildam.technologies.yalla.feature.history.data.paging.OrdersHistoryPagingSource
import uz.ildam.technologies.yalla.feature.history.data.response.OrderHistoryResponse
import uz.ildam.technologies.yalla.feature.history.data.service.OrdersHistoryService
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryModel
import uz.ildam.technologies.yalla.feature.history.domain.model.OrdersHistoryModel
import uz.ildam.technologies.yalla.feature.history.domain.repository.OrderHistoryRepository

class OrderHistoryRepositoryImpl(
    private val service: OrdersHistoryService
) : OrderHistoryRepository {
    override fun getOrdersHistory(): Flow<PagingData<OrdersHistoryModel>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { OrdersHistoryPagingSource(service) }
    ).flow

    override suspend fun getOrderHistory(orderId: Int): Result<OrderHistoryModel, DataError.Network> {
        return when (val result = service.getOrder(orderId)) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(result.data.result.let(OrderHistoryMapper.mapper))
        }
    }
}