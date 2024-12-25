package uz.ildam.technologies.yalla.feature.profile.domain.usecase

import uz.ildam.technologies.yalla.feature.profile.domain.repository.ProfileRepository

class GetMeUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke() = repository.getMe()
}