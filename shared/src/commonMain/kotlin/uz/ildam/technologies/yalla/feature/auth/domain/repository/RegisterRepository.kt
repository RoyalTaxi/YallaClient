package uz.ildam.technologies.yalla.feature.auth.domain.repository

import uz.ildam.technologies.yalla.core.domain.model.DataError
import uz.ildam.technologies.yalla.core.domain.model.Result

interface RegisterRepository {

    suspend fun register(
        phone: String,
        firstName: String,
        lastName: String,
        gender: String,
        dateOfBirth: String,
        key: String
    ): Result<Unit, DataError.Network>
}