package uz.yalla.client.feature.promocode.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.promocode.domain.model.PromocodeActivationModel

interface PromocodeRepository {
    suspend fun activatePromocode(
        value: String
    ): Either<PromocodeActivationModel, DataError.Network>
}