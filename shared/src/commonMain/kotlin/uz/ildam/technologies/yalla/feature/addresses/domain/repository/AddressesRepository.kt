package uz.ildam.technologies.yalla.feature.addresses.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.addresses.domain.model.request.PostOneAddressDto
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressModel

interface AddressesRepository {
    suspend fun findAll(): Either<List<AddressModel>, DataError.Network>
    suspend fun findAllMapAddresses(): Either<List<AddressModel>, DataError.Network>
    suspend fun findOne(id: Int): Either<AddressModel, DataError.Network>
    suspend fun postOne(body: PostOneAddressDto): Either<Unit, DataError.Network>
    suspend fun deleteOne(id: Int): Either<Unit, DataError.Network>
    suspend fun updateOne(id: Int, body: PostOneAddressDto): Either<Unit, DataError.Network>
}