package uz.ildam.technologies.yalla.feature.order.data.response.order

import kotlinx.serialization.Serializable
import uz.ildam.technologies.yalla.core.data.response.ExecutorRemoteModel

@Serializable
data class SearchCarResponse(
    val executors: List<ExecutorRemoteModel>?,
    val timeout: Int?
)