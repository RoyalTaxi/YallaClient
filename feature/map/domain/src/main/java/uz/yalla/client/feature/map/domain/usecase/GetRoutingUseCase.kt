package uz.yalla.client.feature.map.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.map.domain.model.request.GetRoutingDtoItem
import uz.yalla.client.feature.map.domain.model.request.GetRoutingRequestItemDto
import uz.yalla.client.feature.map.domain.model.response.GetRoutingModel
import uz.yalla.client.feature.map.domain.repository.MapRepository

class GetRoutingUseCase(
    private val repository: MapRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(addresses: List<GetRoutingDtoItem>): Result<GetRoutingModel> {
        return withContext(dispatcher) {
            when (val result = repository.getRouting(addresses.map {
                GetRoutingRequestItemDto(
                    type = it.type,
                    lng = it.lng,
                    lat = it.lat
                )
            })) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}