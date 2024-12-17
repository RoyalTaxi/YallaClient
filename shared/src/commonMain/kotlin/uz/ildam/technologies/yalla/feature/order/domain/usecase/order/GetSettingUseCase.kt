package uz.ildam.technologies.yalla.feature.order.domain.usecase.order

import uz.ildam.technologies.yalla.feature.order.domain.repository.OrderRepository

class GetSettingUseCase(
    private val repository: OrderRepository
) {
    suspend operator fun invoke() = repository.getSetting()
}