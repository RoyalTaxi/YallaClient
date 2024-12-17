package uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif

import uz.ildam.technologies.yalla.core.domain.model.ExecutorModel

data class GetTimeOutModel(
    val executors: List<ExecutorModel>,
    val timeout: Int?,
)