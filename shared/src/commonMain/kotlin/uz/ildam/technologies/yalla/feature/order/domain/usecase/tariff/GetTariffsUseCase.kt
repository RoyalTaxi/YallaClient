package uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.ildam.technologies.yalla.feature.order.domain.repository.TariffRepository

class GetTariffsUseCase(
    private val repository: TariffRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(
        optionIds: List<Int> = emptyList(),
        coords: List<Pair<Double, Double>>,
        addressId: Int
    ): Result<GetTariffsModel> {
        return withContext(dispatcher) {
            when (val result = repository.getTariffs(
                optionIds = optionIds,
                cords = coords,
                addressId = addressId
            )) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}