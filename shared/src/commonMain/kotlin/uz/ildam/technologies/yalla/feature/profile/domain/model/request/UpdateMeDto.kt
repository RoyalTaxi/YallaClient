package uz.ildam.technologies.yalla.feature.profile.domain.model.request


data class UpdateMeDto(
    val givenNames: String,
    val surname: String,
    val birthday: String,
    val gender: String,
    val image: String
)