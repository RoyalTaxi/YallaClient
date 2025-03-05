package uz.yalla.client.service.order.response.tariff

import kotlinx.serialization.Serializable
import uz.yalla.client.core.service.model.ExecutorRemoteModel

@Serializable
data class GetTimeOutResponse(
    val executors: List<ExecutorRemoteModel>?,
    val timeout: Int?,
)