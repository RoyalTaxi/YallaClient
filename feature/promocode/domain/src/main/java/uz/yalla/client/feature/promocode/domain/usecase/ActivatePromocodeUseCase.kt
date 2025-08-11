package uz.yalla.client.feature.promocode.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.promocode.domain.model.PromocodeActivationModel
import uz.yalla.client.feature.promocode.domain.repository.PromocodeRepository

class ActivatePromocodeUseCase(
    private val repository: PromocodeRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(
        value: String
    ): Result<PromocodeActivationModel> {
        return withContext(dispatcher) {
            when (val result = repository.activatePromocode(value)) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}