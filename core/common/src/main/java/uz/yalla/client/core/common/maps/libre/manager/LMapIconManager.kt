package uz.yalla.client.core.common.maps.libre.manager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.maps.core.manager.MapIconManager
import uz.yalla.client.core.common.maps.libre.util.LMapConstants
import uz.yalla.client.core.common.maps.libre.util.LMapConstants.DRIVER_ICON_DP
import uz.yalla.client.core.common.maps.libre.util.LMapConstants.INFO_MARKER_WIDTH_DP
import uz.yalla.client.core.common.maps.libre.util.LMapConstants.PIN_ICON_DP
import uz.yalla.client.core.common.utils.InfoMarkerImage
import uz.yalla.client.core.common.utils.createInfoMarkerBitmapWithAnchor
import uz.yalla.client.core.common.utils.dpToPx
import kotlin.math.abs
import kotlin.math.max

class LMapIconManager : MapIconManager {

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

    // Bitmaps for layer-based rendering (MapLibre)
    private var driverBitmap: Bitmap? = null
    private var originBitmap: Bitmap? = null
    private var middleBitmap: Bitmap? = null
    private var destinationBitmap: Bitmap? = null

    // Info marker bitmaps (padded/anchored for SymbolLayer)
    private var originInfoBitmap: Bitmap? = null
    private var destinationInfoBitmap: Bitmap? = null

    // Cache of rotated driver bitmaps (quantized by degrees)
    private val driverRotationCache = mutableMapOf<Int, Bitmap>()

    override fun initializeIcons() {
        val driver = scaleToDp(decode(driverResId), DRIVER_ICON_DP)
        val origin = scaleToDp(decode(originResId), PIN_ICON_DP)
        val middle = scaleToDp(decode(middleResId), PIN_ICON_DP)
        val destination = scaleToDp(decode(destinationResId), PIN_ICON_DP)

        // Layer-based bitmaps
        driverBitmap = driver
        originBitmap = origin
        middleBitmap = middle
        destinationBitmap = destination

        // Reset info markers; created on demand in createMarkerIcons
        originInfoIcon = null
        destinationInfoIcon = null
        originInfoBitmap = null
        destinationInfoBitmap = null
    }

    override fun clearCache() {
        originInfoIcon = null
        destinationInfoIcon = null
        driverBitmap = null
        originBitmap = null
        middleBitmap = null
        destinationBitmap = null
        originInfoBitmap = null
        destinationInfoBitmap = null
        driverRotationCache.values.forEach { it.recycle() }
        driverRotationCache.clear()
    }

    override fun createMarkerIcons(
        carArrivesInMinutes: Int?,
        orderEndsInMinutes: Int?,
        hasOrder: Boolean,
        isDark: Boolean
    ) {
        // Always reset current info bitmaps on each call
        originInfoBitmap = null
        destinationInfoBitmap = null

        // Hide info markers whenever there is an active order
        if (hasOrder) {
            originInfoIcon = null
            destinationInfoIcon = null
            return
        }

        if (carArrivesInMinutes != null) {
            val title = context.getString(
                uz.yalla.client.core.common.R.string.x_min,
                carArrivesInMinutes.coerceAtLeast(0).toString()
            )
            val description = context.getString(uz.yalla.client.core.common.R.string.coming)
            createInfoMarkerBitmapWithAnchor(
                context = context,
                title = title,
                description = description,
                infoColor = context.getColor(R.color.primary),
                pointColor = context.getColor(R.color.info_marker_dot_origin),
                titleColor = context.getColor(R.color.info_marker_title),
                descriptionColor = context.getColor(R.color.info_marker_desc)
            )?.let { img ->
                val padded = centerPadToAnchor(img)
                val scaled = scaleBitmapToDpWidth(padded, INFO_MARKER_WIDTH_DP)
                originInfoBitmap = scaled
            }
        }

        if (orderEndsInMinutes != null) {
            val title = context.getString(
                R.string.x_min,
                orderEndsInMinutes.coerceAtLeast(0).toString()
            )
            val description = context.getString(R.string.on_the_way)
            createInfoMarkerBitmapWithAnchor(
                context = context,
                title = title,
                description = description,
                infoColor = context.getColor(R.color.black),
                pointColor = context.getColor(R.color.info_marker_dot_destination),
                titleColor = context.getColor(R.color.info_marker_title),
                descriptionColor = context.getColor(R.color.info_marker_desc)
            )?.let { img ->
                val padded = centerPadToAnchor(img)
                val scaled = scaleBitmapToDpWidth(padded, INFO_MARKER_WIDTH_DP)
                destinationInfoBitmap = scaled
            }
        }

        // Inform the abstract properties are not used for MapLibre; keep them null
        originInfoIcon = null
        destinationInfoIcon = null
    }

    override fun requireDriverIcon(): Any = requireDriverBitmap()

    override fun requireOriginIcon(): Any = requireOriginBitmap()

    override fun requireMiddleIcon(): Any = requireMiddleBitmap()

    override fun requireDestinationIcon(): Any = requireDestinationBitmap()

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

        val out = createBitmap(newW, newH)
        out.density = Bitmap.DENSITY_NONE
        val canvas = Canvas(out)
        canvas.drawBitmap(src, padLeft.toFloat(), padTop.toFloat(), null)
        return out
    }


    fun requireDriverBitmap(): Bitmap = driverBitmap ?: scaleToDp(decode(driverResId), DRIVER_ICON_DP).also { driverBitmap = it }

    fun requireOriginBitmap(): Bitmap = originBitmap ?: scaleToDp(decode(originResId), PIN_ICON_DP).also { originBitmap = it }

    fun requireMiddleBitmap(): Bitmap = middleBitmap ?: scaleToDp(decode(middleResId), PIN_ICON_DP).also { middleBitmap = it }

    fun requireDestinationBitmap(): Bitmap = destinationBitmap
        ?: scaleToDp(decode(destinationResId), PIN_ICON_DP).also { destinationBitmap = it }

    fun currentOriginInfoBitmap(): Bitmap? = originInfoBitmap
    fun currentDestinationInfoBitmap(): Bitmap? = destinationInfoBitmap

    // Expose context for IconFactory usage in element manager
    fun appContext(): android.content.Context = context

    /** Returns a rotated driver bitmap for the given heading (degrees). Cached in 5° steps. */
    fun driverBitmapRotated(heading: Float): Bitmap {
        val base = requireDriverBitmap()
        val normalized = ((heading % 360f) + 360f) % 360f
        val quant = ((normalized / 5f).toInt()) * 5
        driverRotationCache[quant]?.let { return it }
        val m = Matrix().apply {
            postRotate(normalized, base.width / 2f, base.height / 2f)
        }
        val out = Bitmap.createBitmap(base, 0, 0, base.width, base.height, m, true)
        out.density = Bitmap.DENSITY_NONE
        driverRotationCache[quant] = out
        return out
    }
}
