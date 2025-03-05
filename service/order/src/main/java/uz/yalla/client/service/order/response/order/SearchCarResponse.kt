package uz.yalla.client.service.order.response.order

import kotlinx.serialization.Serializable
import uz.yalla.client.core.service.model.ExecutorRemoteModel

@Serializable
data class SearchCarResponse(
    val executors: List<ExecutorRemoteModel>?,
    val timeout: Int?
)