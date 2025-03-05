package uz.yalla.client.feature.payment.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.payment.domain.model.AddCardModel

interface AddCardRepository {
    suspend fun addCard(number: String, expiry: String): Either<AddCardModel, DataError.Network>

    suspend fun verifyCard(key: String, confirmCode: String): Either<Unit, DataError.Network>
}