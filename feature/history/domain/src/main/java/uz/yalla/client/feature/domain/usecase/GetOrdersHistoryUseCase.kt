package uz.yalla.client.feature.domain.usecase

import app.cash.paging.PagingData
import app.cash.paging.insertSeparators
import app.cash.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import uz.yalla.client.core.domain.formations.TimeFormation.toFormattedDate
import uz.yalla.client.core.domain.formations.TimeFormation.toFormattedPrice
import uz.yalla.client.core.domain.formations.TimeFormation.toFormattedTime
import uz.yalla.client.feature.domain.model.OrdersHistory
import uz.yalla.client.feature.domain.repository.OrderHistoryRepository

class GetOrdersHistoryUseCase(
    private val repository: OrderHistoryRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    operator fun invoke(): Flow<PagingData<OrdersHistory>> =
        repository.getOrdersHistory()
            .map { pagingData ->
                pagingData.map { order ->
                    OrdersHistory.Item(
                        id = order.id,
                        service = order.service,
                        status = order.status,
                        taxi = order.taxi.copy(
                            totalPrice = order.taxi.totalPrice.toFormattedPrice()
                        ),
                        date = order.dateTime.toFormattedDate(),
                        time = order.dateTime.toFormattedTime()
                    )
                }.insertSeparators { before: OrdersHistory.Item?, after: OrdersHistory.Item? ->
                    when {
                        before == null && after == null -> null
                        before == null && after != null -> OrdersHistory.Date(after.date)
                        before != null && after == null -> null
                        before?.date != after?.date -> after?.date?.let { OrdersHistory.Date(it) }
                        else -> null
                    }
                }
            }
            .flowOn(dispatcher)
}