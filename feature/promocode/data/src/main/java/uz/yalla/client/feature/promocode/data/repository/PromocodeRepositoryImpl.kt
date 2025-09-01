package uz.yalla.client.feature.promocode.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.data.ext.mapResult
import uz.yalla.client.feature.promocode.data.mapper.PromocodeMapper
import uz.yalla.client.feature.promocode.domain.model.PromocodeActivationModel
import uz.yalla.client.feature.promocode.domain.repository.PromocodeRepository
import uz.yalla.client.service.promocode.request.PromocodeRequest
import uz.yalla.client.service.promocode.service.PromocodeApiService

class PromocodeRepositoryImpl(
    private val service: PromocodeApiService
) : PromocodeRepository {
    override suspend fun activatePromocode(
        value: String
    ): Either<PromocodeActivationModel, DataError.Network> {
        return service.activatePromocode(PromocodeRequest(value))
            .mapResult(PromocodeMapper.promocodeActivationMapper)
    }
}
