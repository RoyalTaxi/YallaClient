package uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff

import uz.ildam.technologies.yalla.feature.order.domain.repository.OrderRepository

class GetTimeOutUseCase(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(
        lat: Double,
        lng: Double,
        tariffId: Int
    ) = repository.getTimeOut(lat = lat, lng = lng, tariffId = tariffId)
}