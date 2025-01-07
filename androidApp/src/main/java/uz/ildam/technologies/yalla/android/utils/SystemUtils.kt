package uz.ildam.technologies.yalla.android.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun dpToPx(context: Context, dp: Int): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}

fun pxToDp(context: Context, px: Int): Int {
    return (px / context.resources.displayMetrics.density).toInt()
}

fun openPlayMarket(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.yestms.driver"))
        context.startActivity(intent)
    } catch (e: Exception) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=uz.lola.client")
        )
        context.startActivity(intent)
    }
}