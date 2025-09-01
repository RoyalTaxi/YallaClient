package uz.yalla.client.feature.payment.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.data.ext.mapResult
import uz.yalla.client.core.data.ext.mapSuccess
import uz.yalla.client.feature.payment.data.mapper.AddCardMapper
import uz.yalla.client.feature.payment.domain.model.AddCardModel
import uz.yalla.client.feature.payment.domain.repository.AddCardRepository
import uz.yalla.client.service.payment.request.AddCardRequest
import uz.yalla.client.service.payment.request.VerifyCardRequest
import uz.yalla.client.service.payment.service.AddCardApiService

class AddCardRepositoryImpl(
    private val service: AddCardApiService
) : AddCardRepository {
    override suspend fun addCard(
        number: String,
        expiry: String
    ): Either<AddCardModel, DataError.Network> {
        return service.addCard(AddCardRequest(number, expiry))
            .mapResult(AddCardMapper.mapper)
    }

    override suspend fun verifyCard(
        key: String,
        confirmCode: String
    ): Either<Unit, DataError.Network> {
        return service.verifyCard(
            VerifyCardRequest(
                key = key,
                confirm_code = confirmCode
            )
        ).mapSuccess { Unit }
    }
}
