package uz.ildam.technologies.yalla.feature.auth.domain.repository

import uz.ildam.technologies.yalla.feature.auth.domain.model.SendAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.model.ValidateAuthCodeModel

interface AuthRepository {

    suspend fun sendAuthCode(number: String): SendAuthCodeModel

    suspend fun validateAuthCode(number: String, code: Int): ValidateAuthCodeModel
}