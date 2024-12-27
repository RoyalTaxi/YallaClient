package uz.ildam.technologies.yalla.feature.payment.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.payment.domain.model.CardListItemModel
import uz.ildam.technologies.yalla.feature.payment.domain.repository.CardListRepository

class GetCardListUseCase(
    private val repository: CardListRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Result<List<CardListItemModel>> {
        return withContext(dispatcher) {
            when (val result = repository.getCardList()) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}