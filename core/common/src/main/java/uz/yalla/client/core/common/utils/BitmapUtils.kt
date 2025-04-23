package uz.yalla.client.core.common.utils

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View.MeasureSpec
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import uz.yalla.client.core.common.R

@Composable
fun rememberMarker(
    key: String? = null,
    content: @Composable () -> Unit
): BitmapDescriptor? {
    val context = LocalContext.current
    var descriptor by remember { mutableStateOf<BitmapDescriptor?>(null) }
    val layoutRes = R.layout.custom_marker

    LaunchedEffect(key, layoutRes) {
        descriptor = createBitmapDescriptorFromXml(
            context = context,
            layoutRes = layoutRes,
            content = content
        )
    }
    return descriptor
}

private fun createBitmapDescriptorFromXml(
    context: Context,
    @LayoutRes layoutRes: Int,
    content: @Composable () -> Unit
): BitmapDescriptor {
    val markerView = LayoutInflater.from(context).inflate(layoutRes, null, false) as ViewGroup
    val composeView = markerView.findViewById<ComposeView>(R.id.composeView)
    val root = (context as Activity).findViewById<ViewGroup>(android.R.id.content)
    root.addView(
        markerView, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    )
    return try {
        composeView.setContent { content() }
        markerView.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        val w = markerView.measuredWidth
        val h = markerView.measuredHeight
        markerView.layout(0, 0, w, h)
        val bitmap = createBitmap(w, h)
        markerView.draw(Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
    } finally {
        root.removeView(markerView)
    }
}