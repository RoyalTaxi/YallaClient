package uz.ildam.technologies.yalla.feature.payment.domain.usecase

import uz.ildam.technologies.yalla.feature.payment.domain.repository.CardListRepository

class GetCardListUseCase(
    private val repository: CardListRepository
) {
    suspend operator fun invoke() = repository.getCardList()
}