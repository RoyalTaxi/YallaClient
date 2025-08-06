package uz.yalla.client.core.common.maps

import androidx.annotation.ColorInt

object MapConstants {
    @ColorInt
    const val POLYLINE_COLOR_DAY: Int = 0xFF000000.toInt()
    @ColorInt
    const val POLYLINE_COLOR_NIGHT: Int = 0xFFFFFFFF.toInt()

    @ColorInt
    const val DASHED_POLYLINE_COLOR: Int = 0xFF999999.toInt()
    const val POLYLINE_WIDTH = 8f
    const val DASHED_POLYLINE_WIDTH = 4f

    const val DEFAULT_ZOOM = 15f
    const val MIN_ZOOM = 13f
}
