package uz.yalla.client.core.data.mapper

import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.service.model.ExecutorRemoteModel

object ExecutorMapper {
    val mapper: Mapper<ExecutorRemoteModel?, Executor> = { remote ->
        Executor(
            id = remote?.id.or0(),
            lat = remote?.lat.or0(),
            lng = remote?.lng.or0(),
            heading = remote?.heading.or0(),
            distance = remote?.distance.or0()
        )
    }
}