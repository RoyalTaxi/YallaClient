package uz.yalla.client.feature.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.domain.model.OrderHistoryModel
import uz.yalla.client.feature.domain.repository.OrderHistoryRepository

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