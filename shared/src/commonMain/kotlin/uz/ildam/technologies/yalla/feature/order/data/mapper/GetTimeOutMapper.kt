package uz.ildam.technologies.yalla.feature.order.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.feature.order.data.response.tariff.GetTimeOutResponse
import uz.ildam.technologies.yalla.feature.order.domain.model.tarrif.GetTimeOutModel

object GetTimeOutMapper {
    val mapper: Mapper<GetTimeOutResponse?, GetTimeOutModel> = { remote ->
        GetTimeOutModel(
            executors = remote?.executors?.map(executorMapper).orEmpty(),
            timeout = remote?.timeout.or0()
        )
    }

    private val executorMapper: Mapper<GetTimeOutResponse.Executor?, GetTimeOutModel.Executor> =
        { remote ->
            GetTimeOutModel.Executor(
                id = remote?.id.or0(),
                lat = remote?.lat.or0(),
                lng = remote?.lng.or0(),
                heading = remote?.heading.or0(),
                distance = remote?.distance.or0()
            )
        }
}