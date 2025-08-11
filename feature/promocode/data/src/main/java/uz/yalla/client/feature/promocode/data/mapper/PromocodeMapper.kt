package uz.yalla.client.feature.promocode.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.feature.promocode.domain.model.PromocodeActivationModel
import uz.yalla.client.service.promocode.response.PromocodeActivationResponse

object PromocodeMapper {
    val promocodeActivationMapper: Mapper<PromocodeActivationResponse?, PromocodeActivationModel> = { remote ->
        PromocodeActivationModel(
            amount = remote?.amount.or0(),
            message = remote?.message.orEmpty()
        )
    }
}