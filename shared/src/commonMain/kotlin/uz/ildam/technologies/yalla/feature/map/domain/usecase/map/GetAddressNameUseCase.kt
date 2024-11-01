package uz.ildam.technologies.yalla.feature.map.domain.usecase.map

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.feature.map.domain.repository.MapRepository
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.map.domain.model.map.AddressModel


class GetAddressNameUseCase(
    private val repository: MapRepository
) {
    suspend operator fun invoke(lat: Double, lng: Double): Result<AddressModel, DataError.Network> {
        return repository.getAddress(lat, lng)
    }
}