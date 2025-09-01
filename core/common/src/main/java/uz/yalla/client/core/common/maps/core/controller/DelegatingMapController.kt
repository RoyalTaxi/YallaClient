package uz.yalla.client.core.common.maps.core.controller

import com.google.android.gms.maps.GoogleMap
import org.maplibre.android.maps.MapLibreMap
import uz.yalla.client.core.common.maps.google.controller.GMapController
import uz.yalla.client.core.common.maps.libre.controller.LMapController
import uz.yalla.client.core.domain.model.MapPoint

class DelegatingMapController(
    private val google: GMapController,
    private val libre: LMapController
) : MapController {
    private var active: MapController? = null

    override fun attachMap(map: Any) {
        active = when (map) {
            is GoogleMap -> google.also { it.attachMap(map) }
            is MapLibreMap -> libre.also { it.attachMap(map) }
            else -> null
        }
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) { active?.setPadding(left, top, right, bottom) }
    override fun moveTo(point: MapPoint, zoom: Float?) { active?.moveTo(point, zoom) }
    override fun move(point: MapPoint) { active?.move(point) }
    override fun moveWithoutZoom(point: MapPoint) { active?.moveWithoutZoom(point) }
    override fun animateTo(point: MapPoint) { active?.animateTo(point) }
    override fun animate(point: MapPoint, durationMillis: Int) { active?.animate(point, durationMillis) }
    override fun zoomOut() { active?.zoomOut() }
    override fun setGesturesEnabled(enabled: Boolean) { active?.setGesturesEnabled(enabled) }
    override fun setOnCameraIdle(listener: (MapPoint) -> Unit) { active?.setOnCameraIdle(listener) }
    override fun setOnCameraMoveStarted(listener: (isByUser: Boolean) -> Unit) { active?.setOnCameraMoveStarted(listener) }
    override fun getCameraTarget(): MapPoint? = active?.getCameraTarget()
    override fun moveToMyLocation(context: android.content.Context) { active?.moveToMyLocation(context) }
    override fun animateToMyLocation(context: android.content.Context, durationMillis: Int) { active?.animateToMyLocation(context, durationMillis) }
    override fun moveToFitBounds(routing: List<MapPoint>) { active?.moveToFitBounds(routing) }
    override fun animateToFitBounds(routing: List<MapPoint>) { active?.animateToFitBounds(routing) }
    override fun configureDefaultSettings(context: android.content.Context) { active?.configureDefaultSettings(context) }
}

