package uz.ildam.technologies.yalla.feature.order.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.domain.model.tarrif.GetTariffsModel

interface OrderRepository {
    suspend fun getTariffs(
        optionIds: List<Int>,
        coords: List<Pair<Double, Double>>,
        addressId: Int,
    ): Result<GetTariffsModel, DataError.Network>
}