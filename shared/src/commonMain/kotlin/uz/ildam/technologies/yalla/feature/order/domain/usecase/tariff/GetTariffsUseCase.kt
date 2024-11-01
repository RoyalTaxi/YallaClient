package uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.domain.model.tarrif.GetTariffsModel
import uz.ildam.technologies.yalla.feature.order.domain.repository.OrderRepository

class GetTariffsUseCase(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(
        optionIds: List<Int>,
        coords: List<Pair<Double, Double>>,
        addressId: Int
    ): Result<GetTariffsModel, DataError.Network> {
        return repository.getTariffs(
            optionIds = optionIds,
            coords = coords,
            addressId = addressId
        )
    }
}