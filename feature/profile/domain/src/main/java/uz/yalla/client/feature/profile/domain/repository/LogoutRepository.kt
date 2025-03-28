package uz.yalla.client.feature.profile.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either

interface LogoutRepository {
    suspend fun logout(): Either<Unit, DataError.Network>
}