package uz.ildam.technologies.yalla.feature.history.data.repository


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.ildam.technologies.yalla.feature.history.data.paging.OrdersHistoryPagingSource
import uz.ildam.technologies.yalla.feature.history.data.service.OrdersHistoryService
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryModel
import uz.ildam.technologies.yalla.feature.history.domain.repository.OrderHistoryRepository

class OrderHistoryRepositoryImpl(
    private val service: OrdersHistoryService
) : OrderHistoryRepository {
    override fun getOrdersHistory(): Flow<PagingData<OrderHistoryModel>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { OrdersHistoryPagingSource(service) }
    ).flow
}