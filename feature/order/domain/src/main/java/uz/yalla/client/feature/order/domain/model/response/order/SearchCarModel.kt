package uz.yalla.client.feature.order.domain.model.response.order

import uz.yalla.client.core.domain.model.Executor

data class SearchCarModel(
    val executors: List<Executor>,
    val timeout: Int
)