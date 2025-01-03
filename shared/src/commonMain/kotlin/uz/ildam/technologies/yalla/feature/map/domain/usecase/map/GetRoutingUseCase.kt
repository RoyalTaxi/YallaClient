package uz.ildam.technologies.yalla.feature.map.domain.usecase.map

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.map.data.request.map.GetRoutingRequestItem
import uz.ildam.technologies.yalla.feature.map.domain.model.request.GetRoutingDtoItem
import uz.ildam.technologies.yalla.feature.map.domain.model.response.map.GetRoutingModel
import uz.ildam.technologies.yalla.feature.map.domain.repository.MapRepository

class GetRoutingUseCase(
    private val repository: MapRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(addresses: List<GetRoutingDtoItem>): Result<GetRoutingModel> {
        return withContext(dispatcher) {
            when (val result = repository.getRouting(addresses.map {
                GetRoutingRequestItem(
                    type = it.type,
                    lng = it.lng,
                    lat = it.lat
                )
            })) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}