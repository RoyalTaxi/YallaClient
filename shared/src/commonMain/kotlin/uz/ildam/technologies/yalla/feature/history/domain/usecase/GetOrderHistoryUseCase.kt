package uz.ildam.technologies.yalla.feature.history.domain.usecase

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryModel
import uz.ildam.technologies.yalla.feature.history.domain.repository.OrderHistoryRepository

class GetOrderHistoryUseCase(
    private val repository: OrderHistoryRepository
) {
    suspend operator fun invoke(orderId: Int): Result<OrderHistoryModel, DataError.Network> =
        repository.getOrderHistory(orderId)
}