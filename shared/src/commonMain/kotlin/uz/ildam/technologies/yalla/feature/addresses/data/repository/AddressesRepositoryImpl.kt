package uz.ildam.technologies.yalla.feature.addresses.data.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.addresses.data.mapper.AddressesMapper
import uz.ildam.technologies.yalla.feature.addresses.data.service.AddressesApiService
import uz.ildam.technologies.yalla.feature.addresses.domain.model.request.PostOneAddressDto
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressModel
import uz.ildam.technologies.yalla.feature.addresses.domain.repository.AddressesRepository

class AddressesRepositoryImpl(
    private val service: AddressesApiService
) : AddressesRepository {
    override suspend fun findAll(): Either<List<AddressModel>, DataError.Network> {
        return when (val result = service.findAll()) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(
                result.data.result?.map(AddressesMapper.mapper).orEmpty()
            )
        }
    }

    override suspend fun findAllMapAddresses(): Either<List<AddressModel>, DataError.Network> {
        return when (val result = service.findAllMapAddresses()) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(
                result.data.result?.map(AddressesMapper.mapper).orEmpty()
            )
        }
    }

    override suspend fun findOne(id: Int): Either<AddressModel, DataError.Network> {
        return when (val result = service.findOne(id)) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(result.data.result.let(AddressesMapper.mapper))
        }
    }

    override suspend fun postOne(body: PostOneAddressDto): Either<Unit, DataError.Network> {
        return when (val result = service.postOne(body)) {
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
        body: PostOneAddressDto
    ): Either<Unit, DataError.Network> {
        return when (val result = service.updateOne(id, body)) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(Unit)
        }
    }
}