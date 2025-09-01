package uz.yalla.client.core.common.maps.core.controller

import uz.yalla.client.core.domain.model.MapPoint

/**
 * Core map controller interface abstracting camera and UI interactions.
 * Implementations must be provider-specific and hold the actual map reference internally.
 */
interface MapController {
    /** Attach the actual map reference (GoogleMap/MapLibreMap). */
    fun attachMap(map: Any)

    /** Set map padding in pixels. */
    fun setPadding(left: Int, top: Int, right: Int, bottom: Int)

    /** Move camera to a point, optional zoom. */
    fun moveTo(point: MapPoint, zoom: Float? = null)
    /** Backward-compat alias for moveTo. */
    fun move(point: MapPoint) = moveTo(point, null)

    /** Move without changing zoom level. */
    fun moveWithoutZoom(point: MapPoint)

    /** Animate camera to a point. */
    fun animateTo(point: MapPoint)
    /** Animate with optional duration (ignored if provider doesn't support). */
    fun animate(point: MapPoint, durationMillis: Int = 1000) = animateTo(point)

    /** Zoom out one step. */
    fun zoomOut()

    /** Enable or disable interaction gestures as a group. */
    fun setGesturesEnabled(enabled: Boolean)

    /** Camera idle listener with current target position. */
    fun setOnCameraIdle(listener: (MapPoint) -> Unit)

    /** Camera move started with whether it was user gesture. */
    fun setOnCameraMoveStarted(listener: (isByUser: Boolean) -> Unit)

    /** Get current camera target if available. */
    fun getCameraTarget(): MapPoint?

    /** Convenience camera helpers */
    fun moveToMyLocation(context: android.content.Context)
    fun animateToMyLocation(context: android.content.Context, durationMillis: Int = 1000)
    fun moveToFitBounds(routing: List<MapPoint>)
    fun animateToFitBounds(routing: List<MapPoint>)

    /** Apply provider-specific UI defaults (gestures, my-location button, etc.). */
    fun configureDefaultSettings(context: android.content.Context)
}
