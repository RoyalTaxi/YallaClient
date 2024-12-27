package uz.ildam.technologies.yalla.feature.map.domain.usecase.map

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.map.domain.model.map.AddressModel
import uz.ildam.technologies.yalla.feature.map.domain.repository.MapRepository

class GetAddressNameUseCase(
    private val repository: MapRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(lat: Double, lng: Double): Result<AddressModel> {
        return withContext(dispatcher) {
            when (val result = repository.getAddress(lat, lng)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}