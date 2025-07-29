package uz.yalla.client.feature.order.domain.usecase.tariff

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.order.domain.repository.TariffRepository

class GetTariffsUseCase(
    private val repository: TariffRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(
        optionIds: List<Int> = emptyList(),
        coords: List<Pair<Double, Double>>
    ): Result<GetTariffsModel> {
        return withContext(dispatcher) {
            when (val result = repository.getTariffs(
                optionIds = optionIds,
                cords = coords
            )) {
                is Either.Error -> Result.failure(result.error)
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}