import app.cash.paging.PagingData
import app.cash.paging.insertSeparators
import app.cash.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistory
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryDate
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryItem
import uz.ildam.technologies.yalla.feature.history.domain.repository.OrderHistoryRepository

class GetOrderHistoryUseCase(
    private val repository: OrderHistoryRepository
) {
    operator fun invoke(): Flow<PagingData<OrderHistory>> =
        repository.getOrdersHistory().map { pagingData ->
            pagingData.map { order ->
                OrderHistoryItem(
                    id = order.id,
                    service = order.service,
                    status = order.status,
                    taxi = order.taxi,
                    date = order.dateTime.toFormattedDate(),
                    time = order.dateTime.toFormattedTime()
                )
            }.insertSeparators { before: OrderHistoryItem?, after: OrderHistoryItem? ->
                when {
                    before == null && after == null -> null
                    before == null && after != null -> OrderHistoryDate(after.date)
                    before != null && after == null -> null
                    before?.date != after?.date -> after?.date?.let { OrderHistoryDate(it) }
                    else -> null
                }
            }
        }.flowOn(Dispatchers.IO)

    private fun Long.toFormattedDate(): String {
        val dateTime = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        return if (dateTime.year == currentDate.year) {
            "${dateTime.dayOfMonth} ${dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }}"
        } else {
            "${dateTime.dayOfMonth} ${dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${dateTime.year}}"
        }
    }

    private fun Long.toFormattedTime(): String {
        val dateTime = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())
        return "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
    }
}