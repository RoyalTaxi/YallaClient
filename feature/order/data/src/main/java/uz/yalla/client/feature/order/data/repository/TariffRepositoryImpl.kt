package uz.yalla.client.feature.order.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.data.ext.mapResult
import uz.yalla.client.core.data.ext.mapResult
import uz.yalla.client.feature.order.data.mapper.GetTariffMapper
import uz.yalla.client.feature.order.data.mapper.GetTimeOutMapper
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTimeOutModel
import uz.yalla.client.feature.order.domain.repository.TariffRepository
import uz.yalla.client.service.order.request.tariff.GetTariffsRequest
import uz.yalla.client.service.order.request.tariff.GetTimeOutRequest
import uz.yalla.client.service.order.service.TariffApiService

class TariffRepositoryImpl(
    private val service: TariffApiService
) : TariffRepository {
    override suspend fun getTariffs(
        optionIds: List<Int>,
        cords: List<Pair<Double, Double>>
    ): Either<GetTariffsModel, DataError.Network> {

        return service.getTariffs(
            GetTariffsRequest(
                option_ids = optionIds,
                coords = cords.map { GetTariffsRequest.Coordination(it.first, it.second) }
            )
        ).mapResult(GetTariffMapper.mapper)
    }

    override suspend fun getTimeOut(
        lat: Double,
        lng: Double,
        tariffId: Int
    ): Either<GetTimeOutModel, DataError.Network> {
        return service.getTimeOut(
            GetTimeOutRequest(
                lng = lng,
                lat = lat,
                tariff_id = tariffId,
            )
        ).mapResult(GetTimeOutMapper.mapper)
    }
}
