package uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTimeOutModel
import uz.ildam.technologies.yalla.feature.order.domain.repository.TariffRepository

class GetTimeOutUseCase(
    private val repository: TariffRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(
        lat: Double,
        lng: Double,
        tariffId: Int
    ): Result<GetTimeOutModel> {
        return withContext(dispatcher) {
            when (val result = repository.getTimeOut(lat = lat, lng = lng, tariffId = tariffId)) {
                is Either.Error -> Result.failure(Exception(result.error.name))
                is Either.Success -> Result.success(result.data)
            }
        }
    }
}