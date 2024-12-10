package uz.ildam.technologies.yalla.feature.payment.domain.usecase

import uz.ildam.technologies.yalla.feature.payment.domain.repository.AddCardRepository

class VerifyCardUseCase(
    private val repository: AddCardRepository
) {
    suspend operator fun invoke(key: String, confirmCode: String) =
        repository.verifyCard(key = key, confirmCode = confirmCode)
}