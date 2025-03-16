package uz.yalla.client.feature.order.data.mapper

import uz.yalla.client.core.data.mapper.ExecutorMapper
import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.feature.order.domain.model.response.order.SearchCarModel
import uz.yalla.client.service.order.response.order.SearchCarResponse

object SearchCarMapper {
    val mapper: Mapper<SearchCarResponse?, SearchCarModel> = { remote ->
        SearchCarModel(
            executors = remote?.executors?.map(ExecutorMapper.mapper).orEmpty(),
            timeout = remote?.timeout.or0()
        )
    }
}