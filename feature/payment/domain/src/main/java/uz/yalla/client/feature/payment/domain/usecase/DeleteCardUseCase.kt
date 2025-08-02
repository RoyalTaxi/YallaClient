package uz.yalla.client.feature.payment.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.payment.domain.repository.DeleteCardRepository

class DeleteCardUseCase(
    private val repository: DeleteCardRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(cardID: String) : Result<Unit> {
        return withContext(dispatcher) {
            when (val result = repository.deleteCard(cardID = cardID)) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(Unit)
            }
        }
    }
}