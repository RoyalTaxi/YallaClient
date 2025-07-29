package uz.yalla.client.core.common.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import uz.yalla.client.core.common.R

fun createInfoMarkerBitmapDescriptor(
    context: Context,
    title: String?,
    description: String?,
    infoColor: Int,
    pointColor: Int,
    titleColor: Int,
    descriptionColor: Int
): BitmapDescriptor? {
    if (title == null || description == null) {
        return null
    }

    val markerView = LayoutInflater.from(context).inflate(R.layout.info_marker, null)

    val titleText = markerView.findViewById<TextView>(R.id.titleText)
    val descriptionText = markerView.findViewById<TextView>(R.id.descriptionText)
    val infoCard = markerView.findViewById<CardView>(R.id.infoCard)
    val markerPoint = markerView.findViewById<View>(R.id.markerPoint)

    titleText.text = title
    descriptionText.text = description

    infoCard.setCardBackgroundColor(infoColor)
    titleText.setTextColor(titleColor)
    descriptionText.setTextColor(descriptionColor)

    val isDarkMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    val markerDrawable = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        val innerColor = if (isDarkMode) {
            ContextCompat.getColor(context, android.R.color.black)
        } else {
            ContextCompat.getColor(context, android.R.color.white)
        }
        setColor(innerColor)
        val strokeWidthPx = (context.resources.displayMetrics.density * 4).toInt()
        setStroke(strokeWidthPx, pointColor)
    }

    markerPoint.background = markerDrawable

    markerView.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )

    val width = markerView.measuredWidth
    val height = markerView.measuredHeight
    markerView.layout(0, 0, width, height)

    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    markerView.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
