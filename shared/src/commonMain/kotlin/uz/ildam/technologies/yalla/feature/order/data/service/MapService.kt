package uz.ildam.technologies.yalla.feature.order.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.feature.order.data.response.map.PolygonResponseItem
import uz.ildam.technologies.yalla.feature.order.data.url.MapUrl

class MapService(
    private val ktor: HttpClient
) {
    suspend fun getPolygons(): ApiResponseWrapper<List<PolygonResponseItem>> {
        return ktor.get(MapUrl.POLYGON).body()
    }
}