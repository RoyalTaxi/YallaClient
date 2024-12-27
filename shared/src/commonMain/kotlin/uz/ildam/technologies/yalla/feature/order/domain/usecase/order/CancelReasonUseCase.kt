package uz.ildam.technologies.yalla.feature.order.domain.usecase.order

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.order.domain.repository.OrderRepository

class CancelReasonUseCase(
    private val repository: OrderRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(
        orderId: Int,
        reasonId: Int,
        reasonComment: String
    ): Result<Unit> {
        return withContext(dispatcher) {
            when (val result = repository.cancelReason(orderId, reasonId, reasonComment)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}