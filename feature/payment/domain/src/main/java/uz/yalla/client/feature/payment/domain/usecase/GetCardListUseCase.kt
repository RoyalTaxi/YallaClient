package uz.yalla.client.feature.payment.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.payment.domain.model.CardListItemModel
import uz.yalla.client.feature.payment.domain.repository.CardListRepository

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