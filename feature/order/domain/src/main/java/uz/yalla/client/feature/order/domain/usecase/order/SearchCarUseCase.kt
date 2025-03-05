package uz.yalla.client.feature.order.domain.usecase.order

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.order.domain.model.response.order.SearchCarModel
import uz.yalla.client.feature.order.domain.repository.OrderRepository

class SearchCarUseCase(
    private val repository: OrderRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(lat: Double, lng: Double, tariffId: Int): Result<SearchCarModel> {
        return withContext(dispatcher) {
            when (val result = repository.searchCar(lat, lng, tariffId)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}