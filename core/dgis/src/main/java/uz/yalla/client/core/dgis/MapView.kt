package uz.yalla.client.core.dgis

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import ru.dgis.sdk.map.Gesture
import ru.dgis.sdk.map.MapTheme
import ru.dgis.sdk.map.Padding
import ru.dgis.sdk.positioning.DefaultLocationSource
import ru.dgis.sdk.positioning.registerPlatformLocationSource

private class MapNode(
    val camera: CameraNode,
    val objectManager: MapObjectManager,
    private val closeables: List<AutoCloseable>
) : AutoCloseable {
    override fun close() {
        closeables.forEach(AutoCloseable::close)
    }
}

private class MapOptions(
    val onClick: (TouchEvent) -> Unit,
    val cameraState: CameraState
)

private val DefaultCameraPosition = CameraPosition(
    GeoPoint(55.740444, 37.619524),
    Zoom(9.5f)
)

@Composable
fun MapView(
    modifier: Modifier = Modifier,
    onClick: (TouchEvent) -> Unit = {},
    cameraState: CameraState = remember { CameraState(DefaultCameraPosition) },
    content: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    val mapView = remember { mutableStateOf<DGisMapView?>(null) }
    val mapNode = remember { mutableStateOf<MapNode?>(null) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    Box(modifier = modifier) {
        AndroidView(
            factory = {
                val options = MapOptions(onClick, cameraState)
                createDGisMapView(it, options) { map, view ->
                    mapNode.value = createMapNode(context, map, view, options)
                }.apply {
                    mapView.value = this
                    lifecycle.addObserver(this)
                }
            }
        )
        mapNode.value?.let {
            if (content != null) {
                MapProvider(it, content)
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            mapNode.value?.close()
            mapNode.value = null
            mapView.value = null
        }
    }
}

private fun createDGisMapView(
    context: Context,
    options: MapOptions,
    onMapReady: (DGisMap, DGisMapView) -> Unit
): DGisMapView {
    return DGisMapView(context, DGisMapOptions().apply {
        position = options.cameraState.position
    }).apply {
        getMapAsync {
            this.gestureManager?.disableGesture(Gesture.ROTATION)
            this.gestureManager?.disableGesture(Gesture.MULTI_TOUCH_SHIFT)
            this.gestureManager?.disableGesture(Gesture.TILT)
            this.setTheme(MapTheme.defaultTheme)

            this.setCopyrightMargins(
                left = 0,
                top = 0,
                right = dpToPx(context, 8),
                bottom = dpToPx(context, 16),
            )

            registerPlatformLocationSource(
                context = InitMap.context,
                source = DefaultLocationSource(context)
            )

            onMapReady(it, this)
        }
    }
}

private fun createMapNode(
    context: Context,
    map: DGisMap,
    view: DGisMapView,
    options: MapOptions
): MapNode {
    val dgisCamera = map.camera
    val camera = CameraNode(dgisCamera, options.cameraState)
    val objectManager = MapObjectManager(DGisMapObjectManager(map))

    val touchEventProcessor =
        TouchEventProcessor(map, options.onClick, dgisCamera.projection, objectManager)
    view.setTouchEventsObserver(touchEventProcessor)
    val closeables = listOf(map, objectManager, camera, touchEventProcessor)

    dgisCamera.padding = Padding(
        dpToPx(context, 100),
        dpToPx(context, 100),
        dpToPx(context, 100),
        dpToPx(context, 100)
    )

    return MapNode(camera, objectManager, closeables)
}

@Composable
private fun MapProvider(mapNode: MapNode, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalCamera provides mapNode.camera,
        LocalMapObjectManager provides mapNode.objectManager,
    ) {
        content()
    }
}
