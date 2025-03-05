package uz.ildam.technologies.yalla.android2gis

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntSize
import uz.ildam.technologies.yalla.android2gis.Camera
import uz.ildam.technologies.yalla.android2gis.GeoPoint

fun Modifier.snapToGeoPoint(point: GeoPoint, anchor: Alignment = Alignment.Center) = composed {
    val screenPoint = Camera.toScreen(point)
    Modifier.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val width = placeable.width
        val height = placeable.height
        val offset = anchor.align(IntSize.Zero, IntSize(width, height), layoutDirection)
        val x = screenPoint.x.toInt() - offset.x
        val y = screenPoint.y.toInt() - offset.y
        layout(width, height) {
            placeable.place(x, y)
        }
    }
}
