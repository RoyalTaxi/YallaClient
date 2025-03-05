package uz.yalla.client.core.dgis

import android.content.Context

fun dpToPx(context: Context, dp: Int): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}