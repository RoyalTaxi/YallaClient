package uz.ildam.technologies.yalla.feature.auth.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.auth.domain.model.register.RegisterModel

interface RegisterRepository {

    suspend fun register(
        phone: String,
        firstName: String,
        lastName: String,
        gender: String,
        dateOfBirth: String,
        key: String
    ):  Result<RegisterModel, DataError.Network>
}