package uz.ildam.technologies.yalla.feature.order.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.ExecutorMapper
import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.feature.order.data.response.tariff.GetTimeOutResponse
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTimeOutModel

object GetTimeOutMapper {
    val mapper: Mapper<GetTimeOutResponse?, GetTimeOutModel> = { remote ->
        GetTimeOutModel(
            executors = remote?.executors?.map(ExecutorMapper.mapper).orEmpty(),
            timeout = remote?.timeout
        )
    }
}