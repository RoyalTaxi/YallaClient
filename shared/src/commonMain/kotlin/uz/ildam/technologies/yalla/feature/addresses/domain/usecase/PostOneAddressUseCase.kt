package uz.ildam.technologies.yalla.feature.addresses.domain.usecase

import uz.ildam.technologies.yalla.feature.addresses.domain.model.request.PostOneAddressDto
import uz.ildam.technologies.yalla.feature.addresses.domain.repository.AddressesRepository

class PostOneAddressUseCase(
    private val repository: AddressesRepository
) {
    suspend operator fun invoke(body: PostOneAddressDto) = repository.postOne(body)
}