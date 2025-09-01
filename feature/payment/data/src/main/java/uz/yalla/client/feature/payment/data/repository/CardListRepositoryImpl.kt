package uz.yalla.client.feature.payment.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.data.ext.mapResult
import uz.yalla.client.feature.payment.data.mapper.CardListMapper
import uz.yalla.client.feature.payment.domain.model.CardListItemModel
import uz.yalla.client.feature.payment.domain.repository.CardListRepository
import uz.yalla.client.service.payment.service.CardListApiService

class CardListRepositoryImpl(
    private val service: CardListApiService
) : CardListRepository {
    override suspend fun getCardList(): Either<List<CardListItemModel>, DataError.Network> {
        return service.getCardList().mapResult { it?.map(CardListMapper.mapper).orEmpty() }
    }
}
