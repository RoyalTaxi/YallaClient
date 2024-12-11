package uz.ildam.technologies.yalla.feature.payment.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.core.data.mapper.orFalse
import uz.ildam.technologies.yalla.feature.payment.data.response.CardListItemRemoteModel
import uz.ildam.technologies.yalla.feature.payment.domain.model.CardListItemModel

object CardListMapper {
    val mapper: Mapper<CardListItemRemoteModel?, CardListItemModel> = { remote ->
        CardListItemModel(
            cardId = remote?.card_id.orEmpty(),
            default = remote?.default.orFalse(),
            expiry = remote?.expiry.orEmpty(),
            maskedPan = remote?.masked_pan.orEmpty()
        )
    }
}