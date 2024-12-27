package uz.ildam.technologies.yalla.feature.history.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryModel
import uz.ildam.technologies.yalla.feature.history.domain.repository.OrderHistoryRepository

class GetOrderHistoryUseCase(
    private val repository: OrderHistoryRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(orderId: Int): Result<OrderHistoryModel> {
        return withContext(dispatcher) {
            when (val result = repository.getOrderHistory(orderId)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}