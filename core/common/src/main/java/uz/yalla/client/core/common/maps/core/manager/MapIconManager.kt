package uz.yalla.client.core.common.maps.core.manager

import android.content.Context

abstract class MapIconManager(
    protected val context: Context
) {
    open var originInfoIcon: Any? = null
        protected set
    open var destinationInfoIcon: Any? = null
        protected set
    open var originInfoAnchor: Pair<Float, Float>? = null
        protected set
    open var destinationInfoAnchor: Pair<Float, Float>? = null
        protected set
    open var originInfoSizePx: Pair<Int, Int>? = null
        protected set
    open var destinationInfoSizePx: Pair<Int, Int>? = null
        protected set

    abstract fun initializeIcons()
    abstract fun createMarkerIcons(
        carArrivesInMinutes: Int?,
        orderEndsInMinutes: Int?,
        hasOrder: Boolean,
        isDark: Boolean = false
    )

    abstract fun requireDriverIcon(): Any
    abstract fun requireOriginIcon(): Any
    abstract fun requireMiddleIcon(): Any
    abstract fun requireDestinationIcon(): Any
    open fun clearCache() {}

    // Convenience color resolver so element managers can use themeable colors
    open fun color(@androidx.annotation.ColorRes resId: Int): Int =
        androidx.core.content.ContextCompat.getColor(context, resId)
}
