package uz.ildam.technologies.yalla.feature.profile.domain.usecase

import uz.ildam.technologies.yalla.feature.profile.domain.model.request.UpdateMeDto
import uz.ildam.technologies.yalla.feature.profile.domain.repository.ProfileRepository

class UpdateMeUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(body: UpdateMeDto) = repository.updateMe(body)
}