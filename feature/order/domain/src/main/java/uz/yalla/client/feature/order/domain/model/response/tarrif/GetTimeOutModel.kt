package uz.yalla.client.feature.order.domain.model.response.tarrif

import uz.yalla.client.core.domain.model.Executor

data class GetTimeOutModel(
    val executors: List<Executor>,
    val timeout: Int?,
)