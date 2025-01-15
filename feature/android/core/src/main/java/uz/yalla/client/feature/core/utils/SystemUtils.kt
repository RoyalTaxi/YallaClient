package uz.yalla.client.feature.core.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

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

fun Activity.openBrowser(url: String) {
    try {
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = Uri.parse(url)
        startActivity(browserIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
    }
}