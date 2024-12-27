package uz.ildam.technologies.yalla.feature.addresses.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.addresses.domain.model.request.PostOneAddressDto
import uz.ildam.technologies.yalla.feature.addresses.domain.repository.AddressesRepository

class UpdateOneAddressUseCase(
    private val repository: AddressesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(id: Int, body: PostOneAddressDto): Result<Unit> {
        return withContext(dispatcher) {
            when (val result = repository.updateOne(id, body)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(Unit)
            }
        }
    }
}