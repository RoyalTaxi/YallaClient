package uz.yalla.client.core.service.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiPaginationWrapper<T>(
    val list: List<T>?
)