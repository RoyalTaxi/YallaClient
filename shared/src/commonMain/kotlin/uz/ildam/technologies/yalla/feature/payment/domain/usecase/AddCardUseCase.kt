package uz.ildam.technologies.yalla.feature.payment.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.payment.domain.model.AddCardModel
import uz.ildam.technologies.yalla.feature.payment.domain.repository.AddCardRepository

class AddCardUseCase(
    private val repository: AddCardRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(number: String, expiry: String): Result<AddCardModel> {
        return withContext(dispatcher) {
            when (val result = repository.addCard(number, expiry)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}