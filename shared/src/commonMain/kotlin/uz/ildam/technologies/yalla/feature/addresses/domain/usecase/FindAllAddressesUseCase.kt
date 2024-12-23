package uz.ildam.technologies.yalla.feature.addresses.domain.usecase

import uz.ildam.technologies.yalla.feature.addresses.domain.repository.AddressesRepository

class FindAllAddressesUseCase(
    private val repository: AddressesRepository
) {
    suspend operator fun invoke() = repository.findAll()
}