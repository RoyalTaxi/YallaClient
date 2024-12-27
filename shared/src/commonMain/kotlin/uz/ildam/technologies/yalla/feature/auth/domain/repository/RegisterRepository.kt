package uz.ildam.technologies.yalla.feature.auth.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.auth.domain.model.register.RegisterModel

interface RegisterRepository {

    suspend fun register(
        phone: String,
        firstName: String,
        lastName: String,
        gender: String,
        dateOfBirth: String,
        key: String
    ):  Either<RegisterModel, DataError.Network>
}