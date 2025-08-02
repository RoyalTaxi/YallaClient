package uz.yalla.client.feature.payment.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either

interface DeleteCardRepository {
    suspend fun deleteCard(cardID: String) : Either<Unit, DataError.Network>
}