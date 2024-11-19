package uz.ildam.technologies.yalla.core.data.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiPaginationWrapper<T>(
    val list: List<T>?
)