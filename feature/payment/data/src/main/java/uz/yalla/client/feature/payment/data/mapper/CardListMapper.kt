package uz.yalla.client.feature.payment.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.feature.payment.domain.model.CardListItemModel
import uz.yalla.client.service.payment.response.CardListItemRemoteModel

object CardListMapper {
    val mapper: Mapper<CardListItemRemoteModel?, CardListItemModel> = { remote ->
        CardListItemModel(
            cardId = remote?.card_id.orEmpty(),
            maskedPan = remote?.masked_pan.orEmpty()
        )
    }
}