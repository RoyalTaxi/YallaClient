package uz.ildam.technologies.yalla.feature.history.domain.usecase

import app.cash.paging.PagingData
import app.cash.paging.insertSeparators
import app.cash.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistory
import uz.ildam.technologies.yalla.feature.history.domain.repository.OrderHistoryRepository

class GetOrderHistoryUseCase(
    private val repository: OrderHistoryRepository
) {
    operator fun invoke(): Flow<PagingData<OrderHistory>> =
        repository.getOrdersHistory()
            .map { pagingData ->
                pagingData.map { order ->
                    OrderHistory.Item(
                        id = order.id,
                        service = order.service,
                        status = order.status,
                        taxi = order.taxi.copy(
                            totalPrice = order.taxi.totalPrice.toFormattedPrice()
                        ),
                        date = order.dateTime.toFormattedDate(),
                        time = order.dateTime.toFormattedTime()
                    )
                }.insertSeparators { before: OrderHistory.Item?, after: OrderHistory.Item? ->
                    when {
                        before == null && after == null -> null
                        before == null && after != null -> OrderHistory.Date(after.date)
                        before != null && after == null -> null
                        before?.date != after?.date -> after?.date?.let { OrderHistory.Date(it) }
                        else -> null
                    }
                }
            }.flowOn(Dispatchers.IO)

    private fun Long.toFormattedDate(): String {
        val dateTime = Instant.fromEpochMilliseconds(this * 1000L)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val day = dateTime.dayOfMonth.toString().let { if (it.length == 2) it else "0$it" }
        val month = dateTime.month.number.toString().let { if (it.length == 2) it else "0$it" }
        val year = dateTime.year

        return "$day.$month.$year"
    }

    private fun Long.toFormattedTime(): String {
        val dateTime = Instant.fromEpochMilliseconds(this)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return "${dateTime.hour.toString().let { if (it.length == 2) it else "0$it" }}:${
            dateTime.minute.toString().let { if (it.length == 2) it else "0$it" }
        }"
    }

    private fun String.toFormattedPrice(): String {
        return this.reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()
    }
}