package uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import uz.ildam.technologies.yalla.core.domain.global.UseCaseWithParams
import uz.ildam.technologies.yalla.feature.auth.domain.model.SendAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.repository.AuthRepository

class SendAuthCodeUseCase(
    private val service: AuthRepository
) : UseCaseWithParams<String, SendAuthCodeModel>(Dispatchers.IO) {
    override suspend fun execute(parameter: String): SendAuthCodeModel {
        return service.sendAuthCode(parameter)
    }
}