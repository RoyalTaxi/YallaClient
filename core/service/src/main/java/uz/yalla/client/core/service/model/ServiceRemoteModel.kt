package uz.yalla.client.core.service.model

import kotlinx.serialization.Serializable

@Serializable
data class ServiceRemoteModel(
    val cost: Int?,
    val cost_type: String?,
    val id: Int?,
    val name: String?
)