package uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.ildam.technologies.yalla.feature.order.domain.repository.TariffRepository

class GetTariffsUseCase(
    private val repository: TariffRepository
) {
    suspend operator fun invoke(
        optionIds: List<Int> = emptyList(),
        coords: List<Pair<Double, Double>>,
        addressId: Int
    ): Result<GetTariffsModel, DataError.Network> {
        return repository.getTariffs(
            optionIds = optionIds,
            cords = coords,
            addressId = addressId
        )
    }
}