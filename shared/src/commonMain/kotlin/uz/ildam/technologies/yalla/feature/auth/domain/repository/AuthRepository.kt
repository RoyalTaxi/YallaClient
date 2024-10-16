package uz.ildam.technologies.yalla.feature.auth.domain.repository

import uz.ildam.technologies.yalla.core.domain.model.DataError
import uz.ildam.technologies.yalla.core.domain.model.Result
import uz.ildam.technologies.yalla.feature.auth.domain.model.SendAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.model.VerifyAuthCodeModel

interface AuthRepository {

    suspend fun sendAuthCode(number: String): Result<SendAuthCodeModel, DataError.Network>

    suspend fun validateAuthCode(
        number: String,
        code: Int
    ): Result<VerifyAuthCodeModel, DataError.Network>
}