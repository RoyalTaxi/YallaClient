package uz.ildam.technologies.yalla.feature.addresses.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressModel
import uz.ildam.technologies.yalla.feature.addresses.domain.repository.AddressesRepository

class FindAllAddressesUseCase(
    private val repository: AddressesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Result<List<AddressModel>> {
        return withContext(dispatcher) {
            when (val result = repository.findAll()) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}