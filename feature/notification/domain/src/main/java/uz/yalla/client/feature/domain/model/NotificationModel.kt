package uz.yalla.client.feature.domain.model

data class NotificationModel(
    val id: Int,
    val title: String,
    val content: String,
    val dateTime: String,
    val isRead: Boolean,
    val image: String,
)