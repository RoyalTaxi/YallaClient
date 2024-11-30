package uz.ildam.technologies.yalla.feature.order.domain.usecase.order

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.domain.model.request.OrderTaxiDto
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderTaxiModel
import uz.ildam.technologies.yalla.feature.order.domain.repository.OrderRepository

class OrderTaxiUseCase(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(body: OrderTaxiDto): Result<OrderTaxiModel, DataError.Network> =
        repository.orderTaxi(body)
}