package uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif

import uz.ildam.technologies.yalla.core.domain.model.Executor

data class GetTimeOutModel(
    val executors: List<Executor>,
    val timeout: Int?,
)