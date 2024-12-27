package uz.ildam.technologies.yalla.feature.payment.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.payment.data.mapper.AddCardMapper
import uz.ildam.technologies.yalla.feature.payment.data.request.AddCardRequest
import uz.ildam.technologies.yalla.feature.payment.data.request.VerifyCardRequest
import uz.ildam.technologies.yalla.feature.payment.data.service.AddCardApiService
import uz.ildam.technologies.yalla.feature.payment.domain.model.AddCardModel
import uz.ildam.technologies.yalla.feature.payment.domain.repository.AddCardRepository

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