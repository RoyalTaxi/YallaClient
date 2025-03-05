package uz.yalla.client.feature.auth.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.auth.domain.model.register.RegisterModel

interface RegisterRepository {

    suspend fun register(
        phone: String,
        firstName: String,
        lastName: String,
        gender: String,
        dateOfBirth: String,
        key: String
    ): Either<RegisterModel, DataError.Network>
}