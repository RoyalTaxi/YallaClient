package uz.ildam.technologies.yalla.feature.order.domain.usecase.order

import uz.ildam.technologies.yalla.feature.order.domain.repository.OrderRepository

class SearchCarUseCase(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(lat: Double, lng: Double, tariffId: Int) =
        repository.searchCar(lat, lng, tariffId)
}