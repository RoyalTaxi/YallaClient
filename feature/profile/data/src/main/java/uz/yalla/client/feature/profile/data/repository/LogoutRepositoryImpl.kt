package uz.yalla.client.feature.profile.data.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.profile.domain.repository.LogoutRepository
import uz.yalla.client.service.auth.service.LogoutService

class LogoutRepositoryImpl(
    private val service: LogoutService
) : LogoutRepository {
    override suspend fun logout(): Either<Unit, DataError.Network> {
        return when (val result = service.logout()) {
            is Either.Error -> Either.Error(result.error)
            is Either.Success -> Either.Success(Unit)
        }
    }
}