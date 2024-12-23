package uz.ildam.technologies.yalla.feature.addresses.domain.usecase

import uz.ildam.technologies.yalla.feature.addresses.domain.repository.AddressesRepository

class FindOneAddressUseCase(
    private val repository: AddressesRepository
) {
    suspend operator fun invoke(id: Int) = repository.findOne(id)
}