package uz.yalla.client.core.common.maps.manager

import android.content.Context
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.utils.createInfoMarkerBitmapDescriptor
import uz.yalla.client.core.common.utils.vectorToBitmapDescriptor
import uz.yalla.client.core.common.utils.vectorToBitmapDescriptorWithSize

class MapIconManager(
    private val context: Context
) {
    var originIcon: BitmapDescriptor? = null
        private set
    var middleIcon: BitmapDescriptor? = null
        private set
    var destinationIcon: BitmapDescriptor? = null
        private set
    var originInfoIcon: BitmapDescriptor? = null
        private set
    var destinationInfoIcon: BitmapDescriptor? = null
        private set
    var driverIcon: BitmapDescriptor? = null
        private set

    private data class InfoKey(val arrive: Int?, val end: Int?, val has: Boolean, val isDark: Boolean)
    private var lastInfoKey: InfoKey? = null

    fun initializeIcons() {
        if (originIcon == null) {
            originIcon = vectorToBitmapDescriptor(context, R.drawable.ic_origin_marker)
        }
        if (middleIcon == null) {
            middleIcon = vectorToBitmapDescriptor(context, R.drawable.ic_middle_marker)
        }
        if (destinationIcon == null) {
            destinationIcon = vectorToBitmapDescriptor(context, R.drawable.ic_destination_marker)
        }
        if (driverIcon == null) {
            driverIcon = vectorToBitmapDescriptorWithSize(context, R.drawable.img_car_marker, 48)
        }
    }

    fun createMarkerIcons(carArrivesInMinutes: Int?, orderEndsInMinutes: Int?, hasOrder: Boolean, isDark: Boolean = false) {
        val key = InfoKey(carArrivesInMinutes, orderEndsInMinutes, hasOrder, isDark)
        if (key == lastInfoKey) return
        lastInfoKey = key

        initializeIcons()

        originInfoIcon = null
        destinationInfoIcon = null

        if (carArrivesInMinutes != null && !hasOrder) {
            originInfoIcon = createInfoMarkerBitmapDescriptor(
                context = context,
                title = "$carArrivesInMinutes min",
                description = context.getString(R.string.coming),
                infoColor = context.resources.getColor(R.color.primary, context.theme),
                pointColor = context.resources.getColor(R.color.gray, context.theme),
                titleColor = context.resources.getColor(R.color.white, context.theme),
                descriptionColor = context.resources.getColor(R.color.white_50, context.theme)
            )
        }

        if (orderEndsInMinutes != null && !hasOrder) {
            destinationInfoIcon = createInfoMarkerBitmapDescriptor(
                context = context,
                title = "$orderEndsInMinutes min",
                description = context.getString(R.string.on_the_way),
                infoColor = context.resources.getColor(R.color.black, context.theme),
                pointColor = context.resources.getColor(R.color.primary, context.theme),
                titleColor = context.resources.getColor(R.color.white, context.theme),
                descriptionColor = context.resources.getColor(R.color.white_50, context.theme)
            )
        }
    }

    fun requireDriverIcon(): BitmapDescriptor {
        driverIcon?.let { return it }
        initializeIcons()
        return driverIcon ?: BitmapDescriptorFactory.defaultMarker()
    }

    fun requireOriginIcon(): BitmapDescriptor {
        originIcon?.let { return it }
        initializeIcons()
        return originIcon ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
    }

    fun requireMiddleIcon(): BitmapDescriptor {
        middleIcon?.let { return it }
        initializeIcons()
        return middleIcon ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
    }

    fun requireDestinationIcon(): BitmapDescriptor {
        destinationIcon?.let { return it }
        initializeIcons()
        return destinationIcon ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
    }

    fun clearCache() {
        originIcon = null
        middleIcon = null
        destinationIcon = null
        originInfoIcon = null
        destinationInfoIcon = null
        driverIcon = null
        lastInfoKey = null
    }
}
