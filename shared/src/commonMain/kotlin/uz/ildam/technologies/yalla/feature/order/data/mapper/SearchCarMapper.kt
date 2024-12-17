package uz.ildam.technologies.yalla.feature.order.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.ExecutorMapper
import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.feature.order.data.response.order.SearchCarResponse
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SearchCarModel

object SearchCarMapper {
    val mapper: Mapper<SearchCarResponse?, SearchCarModel> = { remote ->
        SearchCarModel(
            executors = remote?.executors?.map(ExecutorMapper.mapper).orEmpty(),
            timeout = remote?.timeout.or0()
        )
    }
}