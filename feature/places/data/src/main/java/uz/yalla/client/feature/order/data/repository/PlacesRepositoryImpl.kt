package uz.yalla.client.feature.order.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.order.data.mapper.PlacesMapper
import uz.yalla.client.feature.order.domain.model.request.PostOnePlaceDto
import uz.yalla.client.feature.order.domain.model.response.PlaceModel
import uz.yalla.client.feature.order.domain.repository.PlacesRepository
import uz.yalla.client.service.places.request.PostOnePlaceRequest
import uz.yalla.client.service.places.service.PlacesApiService

class PlacesRepositoryImpl(
    private val service: PlacesApiService
) : PlacesRepository {
    override suspend fun findAll(): Either<List<PlaceModel>, DataError.Network> {
        return when (val result = service.findAll()) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(
                result.data.result?.map(PlacesMapper.mapper).orEmpty()
            )
        }
    }

    override suspend fun findOne(id: Int): Either<PlaceModel, DataError.Network> {
        return when (val result = service.findOne(id)) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(PlacesMapper.mapper))
        }
    }

    override suspend fun postOne(body: PostOnePlaceDto): Either<Unit, DataError.Network> {
        return when (
            val result = service.postOne(
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
            )
        ) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(Unit)
        }
    }

    override suspend fun deleteOne(id: Int): Either<Unit, DataError.Network> {
        return when (val result = service.deleteOne(id)) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(Unit)
        }
    }

    override suspend fun updateOne(
        id: Int,
        body: PostOnePlaceDto
    ): Either<Unit, DataError.Network> {
        return when (
            val result = service.updateOne(
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
            )
        ) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(Unit)
        }
    }
}