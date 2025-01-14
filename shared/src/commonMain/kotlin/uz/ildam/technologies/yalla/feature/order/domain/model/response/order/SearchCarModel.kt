package uz.ildam.technologies.yalla.feature.order.domain.model.response.order

import uz.ildam.technologies.yalla.core.domain.model.Executor

data class SearchCarModel(
    val executors: List<Executor>,
    val timeout: Int
)