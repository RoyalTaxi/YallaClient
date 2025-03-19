package uz.yalla.client.feature.contact.contact_us.components

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

internal fun Activity.openBrowser(url: String) {
    try {
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = url.toUri()
        startActivity(browserIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
    }
}