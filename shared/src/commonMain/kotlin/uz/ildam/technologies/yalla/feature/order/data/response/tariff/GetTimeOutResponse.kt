package uz.ildam.technologies.yalla.feature.order.data.response.tariff

import kotlinx.serialization.Serializable
import uz.ildam.technologies.yalla.core.data.response.ExecutorRemoteModel

@Serializable
data class GetTimeOutResponse(
    val executors: List<ExecutorRemoteModel>?,
    val timeout: Int?,
)