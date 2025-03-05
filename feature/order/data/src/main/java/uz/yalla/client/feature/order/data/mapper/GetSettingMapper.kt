package uz.yalla.client.feature.order.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.ildam.technologies.yalla.feature.order.data.response.order.SettingResponse

object GetSettingMapper {
    val mapper: Mapper<SettingResponse?, SettingModel> = { remote ->
        SettingModel(
            findRadius = remote?.find_radius.or0(),
            orderCancelTime = remote?.order_cancel_time.or0(),
            reasons = remote?.reasons?.map(reasonMapper).orEmpty()
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