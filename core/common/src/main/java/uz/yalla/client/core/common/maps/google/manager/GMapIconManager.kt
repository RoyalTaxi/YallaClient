package uz.yalla.client.core.common.maps.google.manager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.maps.core.manager.MapIconManager
import uz.yalla.client.core.common.utils.createInfoMarkerBitmap
import uz.yalla.client.core.common.utils.dpToPx

class GMapIconManager : MapIconManager {
    private companion object {
        const val DRIVER_ICON_DP = 48
        const val PIN_ICON_DP = 20
        const val INFO_MARKER_SCALE = 0.85f
    }

    private val driverResId: Int
    private val originResId: Int
    private val middleResId: Int
    private val destinationResId: Int

    constructor(
        context: Context,
        driverResId: Int,
        originResId: Int,
        middleResId: Int,
        destinationResId: Int
    ) : super(context) {
        this.driverResId = driverResId
        this.originResId = originResId
        this.middleResId = middleResId
        this.destinationResId = destinationResId
    }

    private var driverIcon: BitmapDescriptor? = null
    private var originIcon: BitmapDescriptor? = null
    private var middleIcon: BitmapDescriptor? = null
    private var destinationIcon: BitmapDescriptor? = null
    override fun initializeIcons() {
        driverIcon = BitmapDescriptorFactory.fromBitmap(scaleToDp(decode(driverResId), DRIVER_ICON_DP))
        originIcon = BitmapDescriptorFactory.fromBitmap(scaleToDp(decode(originResId), PIN_ICON_DP))
        middleIcon = BitmapDescriptorFactory.fromBitmap(scaleToDp(decode(middleResId), PIN_ICON_DP))
        destinationIcon = BitmapDescriptorFactory.fromBitmap(scaleToDp(decode(destinationResId), PIN_ICON_DP))
        originInfoIcon = null
        destinationInfoIcon = null
    }

    override fun clearCache() {
        // Clear all cached icons so they can be regenerated with new sizes/colors
        driverIcon = null
        originIcon = null
        middleIcon = null
        destinationIcon = null
        originInfoIcon = null
        destinationInfoIcon = null
    }

    override fun createMarkerIcons(
        carArrivesInMinutes: Int?,
        orderEndsInMinutes: Int?,
        hasOrder: Boolean,
        isDark: Boolean
    ) {
        // Hide all info markers when there is an active order
        if (hasOrder) {
            originInfoIcon = null
            destinationInfoIcon = null
            return
        }

        originInfoIcon = if (carArrivesInMinutes != null) {
            val title = context.getString(R.string.x_min, carArrivesInMinutes.coerceAtLeast(0).toString())
            val description = context.getString(R.string.coming)
            createInfoMarkerBitmap(
                context = context,
                title = title,
                description = description,
                infoColor = context.getColor(R.color.info_marker_bg),
                pointColor = context.getColor(R.color.info_marker_dot_origin),
                titleColor = context.getColor(R.color.info_marker_title),
                descriptionColor = context.getColor(R.color.info_marker_desc)
            )?.let { bmp ->
                val w = (bmp.width * INFO_MARKER_SCALE).toInt().coerceAtLeast(1)
                val h = (bmp.height * INFO_MARKER_SCALE).toInt().coerceAtLeast(1)
                val scaled = bmp.scale(w, h)
                BitmapDescriptorFactory.fromBitmap(scaled)
            }
        } else null

        destinationInfoIcon = if (orderEndsInMinutes != null) {
            val title = context.getString(R.string.x_min, orderEndsInMinutes.coerceAtLeast(0).toString())
            val description = context.getString(R.string.on_the_way)
            createInfoMarkerBitmap(
                context = context,
                title = title,
                description = description,
                infoColor = context.getColor(R.color.info_marker_bg),
                pointColor = context.getColor(R.color.info_marker_dot_destination),
                titleColor = context.getColor(R.color.info_marker_title),
                descriptionColor = context.getColor(R.color.info_marker_desc)
            )?.let { bmp ->
                val w = (bmp.width * INFO_MARKER_SCALE).toInt().coerceAtLeast(1)
                val h = (bmp.height * INFO_MARKER_SCALE).toInt().coerceAtLeast(1)
                val scaled = bmp.scale(w, h)
                BitmapDescriptorFactory.fromBitmap(scaled)
            }
        } else null
    }

    override fun requireDriverIcon(): Any = driverIcon
        ?: BitmapDescriptorFactory.fromBitmap(scaleToDp(decode(driverResId), DRIVER_ICON_DP)).also { driverIcon = it }

    override fun requireOriginIcon(): Any = originIcon
        ?: BitmapDescriptorFactory.fromBitmap(scaleToDp(decode(originResId), PIN_ICON_DP)).also { originIcon = it }

    override fun requireMiddleIcon(): Any = middleIcon
        ?: BitmapDescriptorFactory.fromBitmap(scaleToDp(decode(middleResId), PIN_ICON_DP)).also { middleIcon = it }

    override fun requireDestinationIcon(): Any = destinationIcon
        ?: BitmapDescriptorFactory.fromBitmap(scaleToDp(decode(destinationResId), PIN_ICON_DP))
            .also { destinationIcon = it }

    private fun decode(resId: Int): Bitmap {
        val opts = BitmapFactory.Options().apply { inScaled = false }
        val bmp = BitmapFactory.decodeResource(context.resources, resId, opts)
        if (bmp != null) {
            bmp.density = Bitmap.DENSITY_NONE
            return bmp
        }

        // Fallback for vector/XML drawables: render to bitmap
        val drawable = AppCompatResources.getDrawable(context, resId)
            ?: throw IllegalStateException("Drawable resource $resId not found")
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }
        val targetPx = 48
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else targetPx
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else targetPx
        val bitmap = createBitmap(width, height)
        bitmap.density = Bitmap.DENSITY_NONE
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun scaleToDp(src: Bitmap, dp: Int): Bitmap {
        val targetPx = dpToPx(context, dp)
        if (targetPx <= 0) return src
        if (src.width == targetPx && src.height == targetPx) return src
        val out = src.scale(targetPx, targetPx)
        out.density = Bitmap.DENSITY_NONE
        return out
    }
}
