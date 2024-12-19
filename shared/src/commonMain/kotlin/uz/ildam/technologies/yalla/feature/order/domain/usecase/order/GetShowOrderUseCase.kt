package uz.ildam.technologies.yalla.feature.order.domain.usecase.order

import uz.ildam.technologies.yalla.feature.order.domain.repository.OrderRepository

class GetShowOrderUseCase(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(orderId: Int) = repository.getShowOrder(orderId)
}