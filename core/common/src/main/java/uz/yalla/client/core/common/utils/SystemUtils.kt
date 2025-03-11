package uz.yalla.client.core.common.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

fun dpToPx(context: Context, dp: Int): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}

fun pxToDp(context: Context, px: Int): Int {
    return (px / context.resources.displayMetrics.density).toInt()
}

fun openPlayMarket(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, "market://details?id=uz.yalla.client".toUri())
        context.startActivity(intent)
    } catch (e: Exception) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=uz.yalla.client".toUri()
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

suspend fun isFileSizeTooLarge(
    context: Context,
    uri: Uri,
    maxFileSizeInBytes: Long
): Boolean {
    return withContext(Dispatchers.IO) {
        val size = context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { descriptor ->
            descriptor.length
        } ?: 0
        return@withContext size > maxFileSizeInBytes
    }
}

suspend fun isImageDimensionTooLarge(
    context: Context,
    uri: Uri,
    maxWidth: Int,
    maxHeight: Int
): Boolean {
    return withContext(Dispatchers.IO) {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }
        val (width, height) = options.outWidth to options.outHeight
        return@withContext (width > maxWidth || height > maxHeight)
    }
}