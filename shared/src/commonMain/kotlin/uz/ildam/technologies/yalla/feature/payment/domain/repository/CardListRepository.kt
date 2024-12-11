package uz.ildam.technologies.yalla.feature.payment.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.payment.domain.model.CardListItemModel

interface CardListRepository {
    suspend fun getCardList(): Result<List<CardListItemModel>, DataError.Network>
}