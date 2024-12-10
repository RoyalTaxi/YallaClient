package uz.ildam.technologies.yalla.feature.payment.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.feature.payment.data.response.AddCardResponse
import uz.ildam.technologies.yalla.feature.payment.domain.model.AddCardModel

object AddCardMapper {
    val mapper: Mapper<AddCardResponse?, AddCardModel> = { remote ->
        AddCardModel(
            expiry = remote?.expiry.orEmpty(),
            key = remote?.key.orEmpty(),
            number = remote?.number.orEmpty(),
            phone = remote?.phone.orEmpty()
        )
    }
}