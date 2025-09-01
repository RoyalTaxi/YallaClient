package uz.yalla.client.core.common.maps.libre.manager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import org.maplibre.android.annotations.Icon
import org.maplibre.android.annotations.IconFactory
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.maps.core.manager.MapIconManager
import uz.yalla.client.core.common.maps.libre.util.LMapConstants
import uz.yalla.client.core.common.utils.InfoMarkerImage
import uz.yalla.client.core.common.utils.createInfoMarkerBitmapWithAnchor
import uz.yalla.client.core.common.utils.dpToPx
import kotlin.math.abs
import kotlin.math.max

class LMapIconManager : MapIconManager {
    private companion object {
        const val DRIVER_ICON_DP = 20
        const val PIN_ICON_DP = 8
        const val INFO_MARKER_WIDTH_DP = 22
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

    constructor(context: Context) : super(context) {
        this.driverResId = R.drawable.img_car_marker
        this.originResId = R.drawable.ic_origin_marker
        this.middleResId = R.drawable.ic_middle_marker
        this.destinationResId = R.drawable.ic_destination_marker
    }

    private var driverIcon: Icon? = null
    private var originIcon: Icon? = null
    private var middleIcon: Icon? = null
    private var destinationIcon: Icon? = null

    private val iconFactory by lazy { IconFactory.getInstance(context) }

    override fun initializeIcons() {
        driverIcon = iconFactory.fromBitmap(scaleToDp(decode(driverResId), DRIVER_ICON_DP))
        originIcon = iconFactory.fromBitmap(scaleToDp(decode(originResId), PIN_ICON_DP))
        middleIcon = iconFactory.fromBitmap(scaleToDp(decode(middleResId), PIN_ICON_DP))
        destinationIcon = iconFactory.fromBitmap(scaleToDp(decode(destinationResId), PIN_ICON_DP))
        originInfoIcon = null
        destinationInfoIcon = null
    }

    override fun clearCache() {
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
        // Hide info markers whenever there is an active order
        if (hasOrder) {
            originInfoIcon = null
            destinationInfoIcon = null
            return
        }

        originInfoIcon = if (carArrivesInMinutes != null) {
            val title = context.getString(
                uz.yalla.client.core.common.R.string.x_min,
                carArrivesInMinutes.coerceAtLeast(0).toString()
            )
            val description = context.getString(uz.yalla.client.core.common.R.string.coming)
            createInfoMarkerBitmapWithAnchor(
                context = context,
                title = title,
                description = description,
                infoColor = context.getColor(R.color.info_marker_bg),
                pointColor = context.getColor(R.color.info_marker_dot_origin),
                titleColor = context.getColor(R.color.info_marker_title),
                descriptionColor = context.getColor(R.color.info_marker_desc)
            )?.let { img ->
                val padded = centerPadToAnchor(img)
                val scaled = scaleBitmapToDpWidth(padded, INFO_MARKER_WIDTH_DP)
                iconFactory.fromBitmap(scaled)
            }
        } else null

        destinationInfoIcon = if (orderEndsInMinutes != null) {
            val title = context.getString(
                uz.yalla.client.core.common.R.string.x_min,
                orderEndsInMinutes.coerceAtLeast(0).toString()
            )
            val description = context.getString(uz.yalla.client.core.common.R.string.on_the_way)
            createInfoMarkerBitmapWithAnchor(
                context = context,
                title = title,
                description = description,
                infoColor = context.getColor(R.color.info_marker_bg),
                pointColor = context.getColor(R.color.info_marker_dot_destination),
                titleColor = context.getColor(R.color.info_marker_title),
                descriptionColor = context.getColor(uz.yalla.client.core.common.R.color.info_marker_desc)
            )?.let { img ->
                val padded = centerPadToAnchor(img)
                val scaled = scaleBitmapToDpWidth(padded, INFO_MARKER_WIDTH_DP)
                iconFactory.fromBitmap(scaled)
            }
        } else null
    }

    override fun requireDriverIcon(): Any = driverIcon
        ?: iconFactory.fromBitmap(scaleToDp(decode(driverResId), DRIVER_ICON_DP)).also { driverIcon = it }

    override fun requireOriginIcon(): Any = originIcon
        ?: iconFactory.fromBitmap(scaleToDp(decode(originResId), PIN_ICON_DP)).also { originIcon = it }

    override fun requireMiddleIcon(): Any = middleIcon
        ?: iconFactory.fromBitmap(scaleToDp(decode(middleResId), PIN_ICON_DP)).also { middleIcon = it }

    override fun requireDestinationIcon(): Any = destinationIcon
        ?: iconFactory.fromBitmap(scaleToDp(decode(destinationResId), PIN_ICON_DP)).also { destinationIcon = it }

    private fun decode(resId: Int): Bitmap {
        val bmp = BitmapFactory.decodeResource(context.resources, resId)
        if (bmp != null) return bmp

        val drawable = AppCompatResources.getDrawable(context, resId)
            ?: throw IllegalStateException("Drawable resource $resId not found")
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }
        val targetPx = LMapConstants.ICON_SIZE_PX
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else targetPx
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else targetPx
        val bitmap = createBitmap(width, height)
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

    private fun scaleBitmapToDpWidth(src: Bitmap, dpWidth: Int): Bitmap {
        val targetW = dpToPx(context, dpWidth)
        if (targetW <= 0) return src
        val aspect = if (src.width == 0) 1f else src.height.toFloat() / src.width.toFloat()
        val targetH = max(1, (targetW * aspect).toInt())
        val out = src.scale(targetW, targetH)
        out.density = Bitmap.DENSITY_NONE
        return out
    }

    private fun centerPadToAnchor(img: InfoMarkerImage): Bitmap {
        val src = img.bitmap
        val w = src.width
        val h = src.height
        if (w <= 0 || h <= 0) return src

        val cx = img.anchorU * w
        val cy = img.anchorV * h
        val halfW = w / 2f
        val halfH = h / 2f

        val dx = abs(cx - halfW)
        val dy = abs(cy - halfH)

        val padLeft = if (cx < halfW) (2 * dx).toInt() else 0
        val padTop = if (cy < halfH) (2 * dy).toInt() else 0
        val newW = w + (2 * dx).toInt()
        val newH = h + (2 * dy).toInt()

        val out = Bitmap.createBitmap(newW, newH, Bitmap.Config.ARGB_8888)
        out.density = Bitmap.DENSITY_NONE
        val canvas = Canvas(out)
        canvas.drawBitmap(src, padLeft.toFloat(), padTop.toFloat(), null)
        return out
    }
}
