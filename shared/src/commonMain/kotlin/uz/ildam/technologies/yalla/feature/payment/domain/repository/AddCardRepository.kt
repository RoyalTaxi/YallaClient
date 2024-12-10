package uz.ildam.technologies.yalla.feature.payment.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.payment.domain.model.AddCardModel

interface AddCardRepository {
    suspend fun addCard(number: String, expiry: String): Result<AddCardModel, DataError.Network>

    suspend fun verifyCard(key: String, confirmCode: String): Result<Unit, DataError.Network>
}