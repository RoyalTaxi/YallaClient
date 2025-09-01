package uz.yalla.client.core.common.maps.core.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import kotlin.math.max

object IconBadgePainter {
/**
* Draws a round badge with given text on top of base bitmap (top-right by default).
* Returns a NEW bitmap (does not mutate base).
*/
fun drawBadgeTopRight(
base: Bitmap,
text: String,
badgeBg: Int = Color.BLACK,
textColor: Int = Color.WHITE,
paddingPx: Int = 8,
minDiameterPx: Int = 36,
textSizePx: Float = 24f
): Bitmap {
val out = base.copy(Bitmap.Config.ARGB_8888, true)
val c = Canvas(out)
    val paintBg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = badgeBg }
    val paintTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = textSizePx
    }

    val bounds = Rect()
    paintTxt.getTextBounds(text, 0, text.length, bounds)
    val diameter = max(minDiameterPx, max(bounds.width(), bounds.height()) + paddingPx * 2)
    val cx = out.width - diameter / 2f - paddingPx
    val cy = diameter / 2f + paddingPx

    c.drawCircle(cx, cy, diameter / 2f, paintBg)

    val textX = cx - bounds.exactCenterX()
    val textY = cy - bounds.exactCenterY()
    c.drawText(text, textX, textY, paintTxt)

    return out
}
}