package uz.yalla.client.feature.core.utils

import android.content.Context
import android.net.Uri

fun Context.uriToByteArray(uri: Uri): ByteArray? {
    return try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}