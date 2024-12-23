package uz.ildam.technologies.yalla.feature.addresses.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.addresses.data.mapper.AddressesMapper
import uz.ildam.technologies.yalla.feature.addresses.data.service.AddressesApiService
import uz.ildam.technologies.yalla.feature.addresses.domain.model.request.PostOneAddressDto
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressModel
import uz.ildam.technologies.yalla.feature.addresses.domain.repository.AddressesRepository

class AddressesRepositoryImpl(
    private val service: AddressesApiService
) : AddressesRepository {
    override suspend fun findAll(): Result<List<AddressModel>, DataError.Network> {
        return when (val result = service.findAll()) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(
                result.data.result?.map(AddressesMapper.mapper).orEmpty()
            )
        }
    }

    override suspend fun findOne(id: Int): Result<AddressModel, DataError.Network> {
        return when (val result = service.findOne(id)) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(result.data.result.let(AddressesMapper.mapper))
        }
    }

    override suspend fun postOne(body: PostOneAddressDto): Result<Unit, DataError.Network> {
        return when (val result = service.postOne(body)) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(Unit)
        }
    }

    override suspend fun deleteOne(id: Int): Result<Unit, DataError.Network> {
        return when (val result = service.deleteOne(id)) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(Unit)
        }
    }

    override suspend fun updateOne(
        id: Int,
        body: PostOneAddressDto
    ): Result<Unit, DataError.Network> {
        return when (val result = service.updateOne(id, body)) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(Unit)
        }
    }
}