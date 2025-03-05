package uz.yalla.client.feature.payment.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.payment.data.mapper.CardListMapper
import uz.yalla.client.feature.payment.domain.model.CardListItemModel
import uz.yalla.client.feature.payment.domain.repository.CardListRepository
import uz.yalla.client.service.payment.service.CardListApiService

class CardListRepositoryImpl(
    private val service: CardListApiService
) : CardListRepository {
    override suspend fun getCardList(): Either<List<CardListItemModel>, DataError.Network> {
        return when (val result = service.getCardList()) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result?.map(CardListMapper.mapper).orEmpty())
        }
    }
}