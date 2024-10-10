package uz.ildam.technologies.yalla.feature.auth.domain.repository

interface RegisterRepository {

    suspend fun register(
        phone: String,
        firstName: String,
        lastName: String,
        gender: String,
        dateOfBirth: String,
        key: String
    )
}