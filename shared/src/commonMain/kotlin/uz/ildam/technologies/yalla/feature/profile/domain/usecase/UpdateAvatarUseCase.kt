package uz.ildam.technologies.yalla.feature.profile.domain.usecase

import uz.ildam.technologies.yalla.feature.profile.domain.repository.ProfileRepository

class UpdateAvatarUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(image: ByteArray) = repository.updateAvatar(image)
}