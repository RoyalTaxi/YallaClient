package uz.ildam.technologies.yalla.core.data.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseWrapper<T>(
    val success: Boolean?,
    val code: Int?,
    val message: String?,
    val result: T?
)
