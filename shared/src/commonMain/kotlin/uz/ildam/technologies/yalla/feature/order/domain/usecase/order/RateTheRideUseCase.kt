package uz.ildam.technologies.yalla.feature.order.domain.usecase.order

import uz.ildam.technologies.yalla.feature.order.domain.repository.OrderRepository

class RateTheRideUseCase(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(
        ball: Int,
        orderId: Int,
        comment: String
    ) = repository.rateTheRide(
        ball = ball,
        orderId = orderId,
        comment = comment
    )
}