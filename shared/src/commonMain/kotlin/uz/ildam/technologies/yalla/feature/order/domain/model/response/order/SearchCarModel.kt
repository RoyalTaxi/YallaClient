package uz.ildam.technologies.yalla.feature.order.domain.model.response.order

import uz.ildam.technologies.yalla.core.domain.model.ExecutorModel

data class SearchCarModel(
    val executors: List<ExecutorModel>,
    val timeout: Int
)