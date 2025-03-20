package uz.yalla.client.feature.payment.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.feature.payment.domain.model.AddCardModel
import uz.yalla.client.service.payment.response.AddCardResponse

object AddCardMapper {
    val mapper: Mapper<AddCardResponse?, AddCardModel> = { remote ->
        AddCardModel(
            key = remote?.key.orEmpty(),
        )
    }
}