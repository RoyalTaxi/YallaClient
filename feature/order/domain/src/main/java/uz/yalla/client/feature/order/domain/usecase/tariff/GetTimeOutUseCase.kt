package uz.yalla.client.feature.order.domain.usecase.tariff

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTimeOutModel
import uz.yalla.client.feature.order.domain.repository.TariffRepository

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