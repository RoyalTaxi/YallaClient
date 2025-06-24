package uz.yalla.client.feature.auth.domain.repository

import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.auth.domain.model.auth.SendAuthCodeModel
import uz.yalla.client.feature.auth.domain.model.auth.VerifyAuthCodeModel

interface AuthRepository {

    suspend fun sendAuthCode(
        number: String,
        hash: String?
    ): Either<SendAuthCodeModel, DataError.Network>

    suspend fun validateAuthCode(
        number: String,
        code: String
    ): Either<VerifyAuthCodeModel, DataError.Network>
}