package uz.yalla.client.feature.order.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.ildam.technologies.yalla.feature.order.data.response.order.SearchCarResponse
import uz.yalla.client.core.data.mapper.ExecutorMapper

object SearchCarMapper {
    val mapper: Mapper<SearchCarResponse?, SearchCarModel> = { remote ->
        SearchCarModel(
            executors = remote?.executors?.map(ExecutorMapper.mapper).orEmpty(),
            timeout = remote?.timeout.or0()
        )
    }
}