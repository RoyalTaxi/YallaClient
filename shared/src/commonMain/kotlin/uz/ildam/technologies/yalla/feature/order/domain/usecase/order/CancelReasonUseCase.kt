package uz.ildam.technologies.yalla.feature.order.domain.usecase.order

import uz.ildam.technologies.yalla.feature.order.domain.repository.OrderRepository

class CancelReasonUseCase(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(
        orderId: Int,
        reasonId: Int,
        reasonComment: String
    ) = repository.cancelReason(orderId, reasonId, reasonComment)
}