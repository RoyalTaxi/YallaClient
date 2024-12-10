package uz.ildam.technologies.yalla.feature.payment.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
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
    ): Result<AddCardModel, DataError.Network> {
        return when (val result = service.addCard(AddCardRequest(number, expiry))) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(result.data.result.let(AddCardMapper.mapper))
        }
    }

    override suspend fun verifyCard(
        key: String,
        confirmCode: String
    ): Result<Unit, DataError.Network> {
        return when (
            val result = service.verifyCard(
                VerifyCardRequest(
                    key = key,
                    confirm_code = confirmCode
                )
            )
        ) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(Unit)
        }
    }
}