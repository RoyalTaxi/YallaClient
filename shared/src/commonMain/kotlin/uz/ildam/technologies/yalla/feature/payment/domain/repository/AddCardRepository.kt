package uz.ildam.technologies.yalla.feature.payment.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.payment.domain.model.AddCardModel

interface AddCardRepository {
    suspend fun addCard(number: String, expiry: String): Either<AddCardModel, DataError.Network>

    suspend fun verifyCard(key: String, confirmCode: String): Either<Unit, DataError.Network>
}