package uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff

import uz.ildam.technologies.yalla.feature.order.domain.repository.TariffRepository

class GetTimeOutUseCase(
    private val repository: TariffRepository
) {
    suspend operator fun invoke(
        lat: Double,
        lng: Double,
        tariffId: Int
    ) = repository.getTimeOut(lat = lat, lng = lng, tariffId = tariffId)
}