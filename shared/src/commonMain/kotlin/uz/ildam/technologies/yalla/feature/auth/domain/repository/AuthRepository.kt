package uz.ildam.technologies.yalla.feature.auth.domain.repository

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.SendAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.VerifyAuthCodeModel

interface AuthRepository {

    suspend fun sendAuthCode(
        number: String,
        hash: String?
    ): Either<SendAuthCodeModel, DataError.Network>

    suspend fun validateAuthCode(
        number: String,
        code: Int
    ): Either<VerifyAuthCodeModel, DataError.Network>
}