package uz.ildam.technologies.yalla.core.data.response

import kotlinx.serialization.Serializable

@Serializable
data class ExecutorRemoteModel(
    val distance: Double?,
    val heading: Double?,
    val id: Long?,
    val lat: Double?,
    val lng: Double?
)