package uz.yalla.client.core.common.system

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

fun Context.openPlayMarket() {
    try {
        val intent =
            Intent(Intent.ACTION_VIEW, "market://details?id=uz.yalla.client".toUri())
        startActivity(intent)
    } catch (e: Exception) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=uz.yalla.client".toUri()
        )
        startActivity(intent)
    }
}

fun Activity.openBrowser(url: String) {
    try {
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = url.toUri()
        startActivity(browserIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
    }
}