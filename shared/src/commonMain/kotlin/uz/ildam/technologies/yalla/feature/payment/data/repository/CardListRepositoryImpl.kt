package uz.ildam.technologies.yalla.feature.payment.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.payment.data.mapper.CardListMapper
import uz.ildam.technologies.yalla.feature.payment.data.service.CardListApiService
import uz.ildam.technologies.yalla.feature.payment.domain.model.CardListItemModel
import uz.ildam.technologies.yalla.feature.payment.domain.repository.CardListRepository

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