package uz.ildam.technologies.yalla.core.data.mapper

import uz.ildam.technologies.yalla.core.data.response.ExecutorRemoteModel
import uz.ildam.technologies.yalla.core.domain.model.ExecutorModel

object ExecutorMapper {
    val mapper: Mapper<ExecutorRemoteModel?, ExecutorModel> = { remote ->
        ExecutorModel(
            id = remote?.id.or0(),
            lat = remote?.lat.or0(),
            lng = remote?.lng.or0(),
            heading = remote?.heading.or0(),
            distance = remote?.distance.or0()
        )
    }
}