package uz.ildam.technologies.yalla.feature.order.domain.usecase.order

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.order.domain.model.request.OrderTaxiDto
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderTaxiModel
import uz.ildam.technologies.yalla.feature.order.domain.repository.OrderRepository

class OrderTaxiUseCase(
    private val repository: OrderRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(body: OrderTaxiDto): Result<OrderTaxiModel> {
        return withContext(dispatcher) {
            when (val result = repository.orderTaxi(body)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}