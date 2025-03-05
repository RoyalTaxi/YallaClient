package uz.yalla.client.feature.order.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTimeOutModel

interface TariffRepository {
    suspend fun getTariffs(
        optionIds: List<Int>,
        cords: List<Pair<Double, Double>>,
        addressId: Int,
    ): Either<GetTariffsModel, DataError.Network>

    suspend fun getTimeOut(
        lat: Double,
        lng: Double,
        tariffId: Int
    ): Either<GetTimeOutModel, DataError.Network>
}