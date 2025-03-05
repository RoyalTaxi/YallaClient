package uz.yalla.client.feature.payment.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.payment.domain.model.CardListItemModel

interface CardListRepository {
    suspend fun getCardList(): Either<List<CardListItemModel>, DataError.Network>
}