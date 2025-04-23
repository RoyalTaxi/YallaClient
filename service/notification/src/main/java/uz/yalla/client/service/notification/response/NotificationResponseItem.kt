package uz.yalla.client.service.notification.response

import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponseItem(
    val id: Int?,
    val title: String?,
    val content: String?,
    val created_at: Long?,
    val readed: Boolean?,
    val image: String?
)