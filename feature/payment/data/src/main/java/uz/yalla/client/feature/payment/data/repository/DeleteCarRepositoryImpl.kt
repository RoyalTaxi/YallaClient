package uz.yalla.client.feature.payment.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.payment.domain.repository.DeleteCardRepository
import uz.yalla.client.service.payment.service.DeleteCardApiService

class DeleteCarRepositoryImpl(
    private val service: DeleteCardApiService
) : DeleteCardRepository {
    override suspend fun deleteCard(cardID: String): Either<Unit, DataError.Network> {
        return when (
            val result = service.deleteCard(cardID)) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(Unit)
        }
    }
}