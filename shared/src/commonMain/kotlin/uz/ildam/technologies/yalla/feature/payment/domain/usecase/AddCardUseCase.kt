package uz.ildam.technologies.yalla.feature.payment.domain.usecase

import uz.ildam.technologies.yalla.feature.payment.domain.repository.AddCardRepository

class AddCardUseCase(
    private val repository: AddCardRepository
) {
    suspend operator fun invoke(number: String, expiry: String) = repository.addCard(number, expiry)
}