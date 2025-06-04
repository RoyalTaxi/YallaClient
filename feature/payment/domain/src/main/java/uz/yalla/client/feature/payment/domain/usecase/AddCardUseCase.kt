package uz.yalla.client.feature.payment.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.payment.domain.model.AddCardModel
import uz.yalla.client.feature.payment.domain.repository.AddCardRepository

class AddCardUseCase(
    private val repository: AddCardRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(number: String, expiry: String): Result<AddCardModel> {
        return withContext(dispatcher) {
            when (val result = repository.addCard(number, expiry)) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}