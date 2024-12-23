package uz.ildam.technologies.yalla.feature.order.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.data.mapper.GetTariffMapper
import uz.ildam.technologies.yalla.feature.order.data.mapper.GetTimeOutMapper
import uz.ildam.technologies.yalla.feature.order.data.request.tariff.GetTariffsRequest
import uz.ildam.technologies.yalla.feature.order.data.request.tariff.GetTimeOutRequest
import uz.ildam.technologies.yalla.feature.order.data.service.TariffApiService
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTimeOutModel
import uz.ildam.technologies.yalla.feature.order.domain.repository.TariffRepository

class TariffRepositoryImpl(
    private val service: TariffApiService
) : TariffRepository {
    override suspend fun getTariffs(
        optionIds: List<Int>,
        cords: List<Pair<Double, Double>>,
        addressId: Int
    ): Result<GetTariffsModel, DataError.Network> {

        return when (val result = service.getTariffs(
            GetTariffsRequest(
                option_ids = optionIds,
                coords = cords.map { GetTariffsRequest.Coordination(it.first, it.second) },
                address_id = addressId
            )
        )) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(result.data.result.let(GetTariffMapper.mapper))
        }
    }

    override suspend fun getTimeOut(
        lat: Double,
        lng: Double,
        tariffId: Int
    ): Result<GetTimeOutModel, DataError.Network> {
        return when (val result = service.getTimeOut(
            GetTimeOutRequest(
                lng = lng,
                lat = lat,
                tariff_id = tariffId,
            )
        )) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(result.data.result.let(GetTimeOutMapper.mapper))
        }
    }
}