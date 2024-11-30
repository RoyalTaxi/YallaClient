package uz.ildam.technologies.yalla.feature.order.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTimeOutModel

interface TariffRepository {
    suspend fun getTariffs(
        optionIds: List<Int>,
        cords: List<Pair<Double, Double>>,
        addressId: Int,
    ): Result<GetTariffsModel, DataError.Network>

    suspend fun getTimeOut(
        lat: Double,
        lng: Double,
        tariffId: Int
    ): Result<GetTimeOutModel, DataError.Network>
}