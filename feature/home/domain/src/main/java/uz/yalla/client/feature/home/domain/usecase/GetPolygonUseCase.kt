package uz.yalla.client.feature.home.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.home.domain.model.response.PolygonRemoteItem
import uz.yalla.client.feature.home.domain.repository.HomeRepository

class GetPolygonUseCase(
    private val repository: HomeRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Result<List<PolygonRemoteItem>> {
        return withContext(dispatcher) {
            when (val result = repository.getPolygon()) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}