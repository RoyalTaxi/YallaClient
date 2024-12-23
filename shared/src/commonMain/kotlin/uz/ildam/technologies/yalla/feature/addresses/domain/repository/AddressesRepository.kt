package uz.ildam.technologies.yalla.feature.addresses.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.addresses.domain.model.request.PostOneAddressDto
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressModel

interface AddressesRepository {
    suspend fun findAll(): Result<List<AddressModel>, DataError.Network>
    suspend fun findOne(id: Int): Result<AddressModel, DataError.Network>
    suspend fun postOne(body: PostOneAddressDto): Result<Unit, DataError.Network>
    suspend fun deleteOne(id: Int): Result<Unit, DataError.Network>
    suspend fun updateOne(id: Int, body: PostOneAddressDto): Result<Unit, DataError.Network>
}