package uz.yalla.client.feature.order.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.data.mapper.orFalse
import uz.yalla.client.feature.order.domain.model.response.order.SettingModel
import uz.yalla.client.service.order.response.order.SettingResponse

object GetSettingMapper {
    val mapper: Mapper<SettingResponse?, SettingModel> = { remote ->
        SettingModel(
            findRadius = remote?.find_radius.or0(),
            orderCancelTime = remote?.order_cancel_time.or0(),
            reasons = remote?.reasons?.map(reasonMapper).orEmpty(),
            minBonus = remote?.min_bonus.or0(),
            maxBonus = remote?.max_bonus.or0(),
            isBonusEnabled = remote?.use_the_bonus.orFalse()
        )
    }

    private val reasonMapper: Mapper<SettingResponse.CancelReason?, SettingModel.CancelReason> =
        { remote ->
            SettingModel.CancelReason(
                id = remote?.id.or0(),
                name = remote?.name.orEmpty()
            )
        }
}