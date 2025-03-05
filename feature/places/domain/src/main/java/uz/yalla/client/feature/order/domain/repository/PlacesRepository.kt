package uz.yalla.client.feature.order.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.domain.model.PlaceModel
import uz.yalla.client.feature.order.domain.model.request.PostOnePlaceDto

interface PlacesRepository {
    suspend fun findAll(): Either<List<PlaceModel>, DataError.Network>
    suspend fun findAllMapAddresses(): Either<List<PlaceModel>, DataError.Network>
    suspend fun findOne(id: Int): Either<PlaceModel, DataError.Network>
    suspend fun postOne(body: PostOnePlaceDto): Either<Unit, DataError.Network>
    suspend fun deleteOne(id: Int): Either<Unit, DataError.Network>
    suspend fun updateOne(id: Int, body: PostOnePlaceDto): Either<Unit, DataError.Network>
}