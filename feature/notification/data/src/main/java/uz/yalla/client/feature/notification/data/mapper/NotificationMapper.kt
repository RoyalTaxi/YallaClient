package uz.yalla.client.feature.notification.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.formations.TimeFormation.toFormattedDate
import uz.yalla.client.core.domain.formations.TimeFormation.toFormattedTime
import uz.yalla.client.feature.domain.model.NotificationModel
import uz.yalla.client.service.notification.response.NotificationResponse

object NotificationMapper {
    val mapper: Mapper<NotificationResponse?, NotificationModel> = { remote ->
        NotificationModel(
            id = remote?.id.or0(),
            title = remote?.title.orEmpty(),
            content = remote?.content.orEmpty(),
            dateTime = "${remote?.created_at?.toFormattedDate()} ${remote?.created_at?.toFormattedTime()}",
            isRead = remote?.readed ?: false,
            image = remote?.image.orEmpty(),
        )
    }
}