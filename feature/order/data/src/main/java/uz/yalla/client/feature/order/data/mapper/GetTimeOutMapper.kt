package uz.yalla.client.feature.order.data.mapper

import uz.yalla.client.core.data.mapper.ExecutorMapper
import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTimeOutModel
import uz.yalla.client.service.order.response.tariff.GetTimeOutResponse

object GetTimeOutMapper {
    val mapper: Mapper<GetTimeOutResponse?, GetTimeOutModel> = { remote ->
        GetTimeOutModel(
            executors = remote?.executors?.map(ExecutorMapper.mapper).orEmpty(),
            timeout = remote?.timeout
        )
    }
}