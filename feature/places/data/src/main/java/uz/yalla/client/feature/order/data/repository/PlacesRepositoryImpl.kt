package uz.yalla.client.feature.order.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.data.ext.mapResult
import uz.yalla.client.core.data.ext.mapSuccess
import uz.yalla.client.feature.order.data.mapper.PlacesMapper
import uz.yalla.client.feature.order.domain.model.request.PostOnePlaceDto
import uz.yalla.client.feature.order.domain.model.response.PlaceModel
import uz.yalla.client.feature.order.domain.repository.PlacesRepository
import uz.yalla.client.service.places.request.PostOnePlaceRequest
import uz.yalla.client.service.places.service.PlacesApiService

class PlacesRepositoryImpl(
    private val service: PlacesApiService
) : PlacesRepository {
    override suspend fun findAll(): Either<List<PlaceModel>, DataError.Network> =
        service.findAll().mapResult { it?.map(PlacesMapper.mapper).orEmpty() }

    override suspend fun findOne(id: Int): Either<PlaceModel, DataError.Network> =
        service.findOne(id).mapResult(PlacesMapper.mapper)

    override suspend fun postOne(body: PostOnePlaceDto): Either<Unit, DataError.Network> =
        service.postOne(
            body = PostOnePlaceRequest(
                name = body.name,
                address = body.address,
                lat = body.lat,
                lng = body.lng,
                type = body.type,
                enter = body.enter,
                apartment = body.apartment,
                floor = body.floor,
                comment = body.comment
            )
        ).mapSuccess { Unit }

    override suspend fun deleteOne(id: Int): Either<Unit, DataError.Network> =
        service.deleteOne(id).mapSuccess { Unit }

    override suspend fun updateOne(
        id: Int,
        body: PostOnePlaceDto
    ): Either<Unit, DataError.Network> =
        service.updateOne(
            id = id,
            body = PostOnePlaceRequest(
                name = body.name,
                address = body.address,
                lat = body.lat,
                lng = body.lng,
                type = body.type,
                enter = body.enter,
                apartment = body.apartment,
                floor = body.floor,
                comment = body.comment
            )
        ).mapSuccess { Unit }
}
