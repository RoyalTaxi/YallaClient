package uz.yalla.client.feature.payment.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
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
        return when (val result = service.addCard(AddCardRequest(number, expiry))) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(AddCardMapper.mapper))
        }
    }

    override suspend fun verifyCard(
        key: String,
        confirmCode: String
    ): Either<Unit, DataError.Network> {
        return when (
            val result = service.verifyCard(
                VerifyCardRequest(
                    key = key,
                    confirm_code = confirmCode
                )
            )
        ) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(Unit)
        }
    }
}