package uz.ildam.technologies.yalla.feature.payment.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.payment.domain.repository.AddCardRepository

class VerifyCardUseCase(
    private val repository: AddCardRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(key: String, confirmCode: String): Result<Unit> {
        return withContext(dispatcher) {
            when (val result = repository.verifyCard(key = key, confirmCode = confirmCode)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(Unit)
            }
        }
    }
}