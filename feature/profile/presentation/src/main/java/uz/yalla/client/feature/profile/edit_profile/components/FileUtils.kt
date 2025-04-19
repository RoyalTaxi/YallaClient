package uz.yalla.client.feature.profile.edit_profile.components

import android.content.Context
import android.net.Uri

internal fun Context.uriToByteArray(uri: Uri): ByteArray? {
    return try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}